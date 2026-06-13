package com.example.neurodeck.data.repository

import app.cash.sqldelight.db.SqlDriver
import com.example.neurodeck.data.local.NeuroDeckDatabase
import com.example.neurodeck.data.local.TestDatabaseFactory
import com.example.neurodeck.domain.usecase.CalculateNextReviewUseCase
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Integration test untuk [ReviewRecordRepositoryImpl].
 *
 * Fokus ke logika non-trivial: streak (timezone-aware), accuracy, dan
 * daily activity. Semua timestamp dibangun dari LocalDate pada timezone sistem
 * yang sama dengan implementasi (TimeZone.currentSystemDefault()) di jam siang
 * (12:00) supaya pengelompokan per-hari deterministic dan tidak terpengaruh
 * boundary tengah malam.
 */
class ReviewRecordRepositoryImplTest {

    private lateinit var driver: SqlDriver
    private lateinit var database: NeuroDeckDatabase
    private lateinit var repository: ReviewRecordRepositoryImpl
    private val tz = TimeZone.currentSystemDefault()

    @BeforeTest
    fun setup() {
        driver = TestDatabaseFactory.createDriver()
        database = TestDatabaseFactory.create(driver)
        repository = ReviewRecordRepositoryImpl(database)
    }

    @AfterTest
    fun teardown() {
        driver.close()
    }

    private val today: LocalDate get() = Clock.System.now().toLocalDateTime(tz).date

    /** Instant jam 12:00 siang pada tanggal tertentu, di timezone sistem. */
    private fun noonOf(date: LocalDate): Instant =
        LocalDateTime(date.year, date.monthNumber, date.dayOfMonth, 12, 0).toInstant(tz)

    /** Insert review record langsung dengan cardId dummy + rating + waktu. */
    private fun insertReview(cardId: Long, rating: Int, at: Instant) {
        database.reviewRecordQueries.insert(
            cardId = cardId,
            rating = rating.toLong(),
            reviewedAt = at.toEpochMilliseconds(),
        )
    }

    /** Buat 1 deck + 1 kartu supaya FK reviewRecord.cardId valid. */
    private suspend fun seedCard(): Long {
        val deckRepo = DeckRepositoryImpl(database)
        val cardRepo = CardRepositoryImpl(database, CalculateNextReviewUseCase())
        val deckId = deckRepo.createDeck("D", "")
        return cardRepo.createCard(deckId, "f", "b")
    }

    @Test
    fun `getTotalReviews menghitung semua record`() = runTest {
        val cardId = seedCard()
        insertReview(cardId, 4, noonOf(today))
        insertReview(cardId, 5, noonOf(today))
        assertEquals(2, repository.getTotalReviews())
    }

    @Test
    fun `getStreakDays nol saat belum ada review`() = runTest {
        assertEquals(0, repository.getStreakDays(Clock.System.now()))
    }

    @Test
    fun `getStreakDays menghitung hari berturut-turut`() = runTest {
        val cardId = seedCard()
        // Review hari ini, kemarin, dan lusa kemarin (3 hari berturut-turut)
        insertReview(cardId, 4, noonOf(today))
        insertReview(cardId, 4, noonOf(today.minus(1, DateTimeUnit.DAY)))
        insertReview(cardId, 4, noonOf(today.minus(2, DateTimeUnit.DAY)))

        assertEquals(3, repository.getStreakDays(noonOf(today)))
    }

    @Test
    fun `getStreakDays putus saat ada gap`() = runTest {
        val cardId = seedCard()
        // Hari ini ada, lalu lompat (tidak ada kemarin), 3 hari lalu ada
        insertReview(cardId, 4, noonOf(today))
        insertReview(cardId, 4, noonOf(today.minus(3, DateTimeUnit.DAY)))

        assertEquals(1, repository.getStreakDays(noonOf(today)), "Streak putus di gap")
    }

    @Test
    fun `getStreakDays tetap valid kalau hari ini belum review tapi kemarin ada`() = runTest {
        val cardId = seedCard()
        insertReview(cardId, 4, noonOf(today.minus(1, DateTimeUnit.DAY)))
        insertReview(cardId, 4, noonOf(today.minus(2, DateTimeUnit.DAY)))

        // now = hari ini, tapi review terakhir kemarin → streak masih 2
        assertEquals(2, repository.getStreakDays(noonOf(today)))
    }

    @Test
    fun `countReviewsInRange hanya menghitung dalam rentang`() = runTest {
        val cardId = seedCard()
        insertReview(cardId, 4, noonOf(today))
        insertReview(cardId, 4, noonOf(today.minus(1, DateTimeUnit.DAY)))
        insertReview(cardId, 4, noonOf(today.minus(10, DateTimeUnit.DAY))) // di luar range

        val from = noonOf(today.minus(2, DateTimeUnit.DAY))
        val to = noonOf(today).plus(1, DateTimeUnit.HOUR, tz) // sedikit setelah noon hari ini
        assertEquals(2, repository.countReviewsInRange(from, to))
    }

    @Test
    fun `getAccuracyInRange nol saat tidak ada review`() = runTest {
        val from = noonOf(today.minus(7, DateTimeUnit.DAY))
        val to = noonOf(today).plus(1, DateTimeUnit.DAY, tz)
        assertEquals(0.0, repository.getAccuracyInRange(from, to))
    }

    @Test
    fun `getAccuracyInRange menghitung rasio passing`() = runTest {
        val cardId = seedCard()
        // 3 passing (rating >= 3) + 1 gagal (AGAIN = 0) = 75%
        insertReview(cardId, 4, noonOf(today))
        insertReview(cardId, 5, noonOf(today))
        insertReview(cardId, 3, noonOf(today))
        insertReview(cardId, 0, noonOf(today))

        val from = noonOf(today.minus(1, DateTimeUnit.DAY))
        val to = noonOf(today).plus(1, DateTimeUnit.DAY, tz)
        assertEquals(0.75, repository.getAccuracyInRange(from, to))
    }

    @Test
    fun `getDailyActivity mengelompokkan per offset hari`() = runTest {
        val cardId = seedCard()
        // 2 review hari ini (offset 0), 1 kemarin (offset 1)
        insertReview(cardId, 4, noonOf(today))
        insertReview(cardId, 4, noonOf(today))
        insertReview(cardId, 4, noonOf(today.minus(1, DateTimeUnit.DAY)))

        val activity = repository.getDailyActivity(daysBack = 7, now = noonOf(today))
        assertEquals(7, activity.size, "Map punya entri untuk tiap hari dalam window")
        assertEquals(2, activity[0], "Hari ini 2 review")
        assertEquals(1, activity[1], "Kemarin 1 review")
        assertEquals(0, activity[5], "Hari tanpa review = 0")
    }

    @Test
    fun `getReviewedToday hanya menghitung review hari ini`() = runTest {
        val cardId = seedCard()
        insertReview(cardId, 4, noonOf(today))
        insertReview(cardId, 4, noonOf(today.minus(1, DateTimeUnit.DAY)))

        assertTrue(repository.getReviewedToday(noonOf(today)) >= 1)
        assertEquals(1, repository.getReviewedToday(noonOf(today)))
    }
}
