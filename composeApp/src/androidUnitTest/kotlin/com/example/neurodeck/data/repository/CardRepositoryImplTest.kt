package com.example.neurodeck.data.repository

import app.cash.sqldelight.db.SqlDriver
import com.example.neurodeck.data.local.NeuroDeckDatabase
import com.example.neurodeck.data.local.TestDatabaseFactory
import com.example.neurodeck.domain.model.ReviewRating
import com.example.neurodeck.domain.usecase.CalculateNextReviewUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.plus
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Integration test untuk [CardRepositoryImpl] — termasuk operasi paling kompleks:
 * [CardRepositoryImpl.reviewCard] yang melibatkan SM-2 + transaction (update kartu
 * + insert review record), serta cascade delete via foreign key.
 */
class CardRepositoryImplTest {

    private lateinit var driver: SqlDriver
    private lateinit var database: NeuroDeckDatabase
    private lateinit var deckRepo: DeckRepositoryImpl
    private lateinit var cardRepo: CardRepositoryImpl
    private var deckId: Long = 0L

    @BeforeTest
    fun setup() {
        driver = TestDatabaseFactory.createDriver()
        database = TestDatabaseFactory.create(driver)
        deckRepo = DeckRepositoryImpl(database)
        cardRepo = CardRepositoryImpl(database, CalculateNextReviewUseCase())
        // Tiap test butuh deck dulu (foreign key). runTest tidak bisa di @BeforeTest,
        // jadi kita buat deck di sini via blocking lewat coroutine kecil di tiap test.
    }

    @AfterTest
    fun teardown() {
        driver.close()
    }

    private suspend fun ensureDeck(): Long {
        if (deckId == 0L) deckId = deckRepo.createDeck("Deck Test", "")
        return deckId
    }

    @Test
    fun `createCard menyimpan kartu dengan default SM-2`() = runTest {
        val dId = ensureDeck()
        val cardId = cardRepo.createCard(dId, "Apa itu KMP?", "Kotlin Multiplatform")
        assertTrue(cardId > 0)

        val card = cardRepo.getCardById(cardId)!!
        assertEquals("Apa itu KMP?", card.front)
        assertEquals(0, card.reviewState.repetitions, "Kartu baru repetitions=0")
        assertEquals(2.5, card.reviewState.easeFactor)
    }

    @Test
    fun `getCardById mengembalikan null untuk id tidak ada`() = runTest {
        assertNull(cardRepo.getCardById(404L))
    }

    @Test
    fun `observeCardsByDeck mengembalikan kartu deck tersebut`() = runTest {
        val dId = ensureDeck()
        cardRepo.createCard(dId, "f1", "b1")
        cardRepo.createCard(dId, "f2", "b2")

        val cards = cardRepo.observeCardsByDeck(dId).first()
        assertEquals(2, cards.size)
    }

    @Test
    fun `createCards bulk insert mengembalikan semua id`() = runTest {
        val dId = ensureDeck()
        val ids = cardRepo.createCards(
            dId,
            listOf("f1" to "b1", "f2" to "b2", "f3" to "b3"),
        )
        assertEquals(3, ids.size)
        assertEquals(3, cardRepo.observeCardsByDeck(dId).first().size)
    }

    @Test
    fun `updateCardContent mengubah front dan back`() = runTest {
        val dId = ensureDeck()
        val cardId = cardRepo.createCard(dId, "lama", "lama")

        cardRepo.updateCardContent(cardId, "front baru", "back baru")

        val card = cardRepo.getCardById(cardId)!!
        assertEquals("front baru", card.front)
        assertEquals("back baru", card.back)
    }

    @Test
    fun `deleteCard menghapus kartu`() = runTest {
        val dId = ensureDeck()
        val cardId = cardRepo.createCard(dId, "f", "b")
        cardRepo.deleteCard(cardId)
        assertNull(cardRepo.getCardById(cardId))
    }

    @Test
    fun `reviewCard dengan GOOD memperbarui state SM-2 dan menambah review record`() = runTest {
        val dId = ensureDeck()
        val cardId = cardRepo.createCard(dId, "f", "b")
        val now = Instant.parse("2026-05-19T10:00:00Z")

        cardRepo.reviewCard(cardId, ReviewRating.GOOD, now)

        val card = cardRepo.getCardById(cardId)!!
        assertEquals(1, card.reviewState.repetitions, "Review GOOD pertama => repetitions=1")
        assertEquals(1, card.reviewState.intervalDays, "Interval pertama = 1 hari")
        // Review record tercatat untuk statistik
        val totalReviews = database.reviewRecordQueries.countAll().executeAsOne()
        assertEquals(1L, totalReviews)
    }

    @Test
    fun `reviewCard memajukan dueAt ke masa depan`() = runTest {
        val dId = ensureDeck()
        val cardId = cardRepo.createCard(dId, "f", "b")
        val now = Instant.parse("2026-05-19T10:00:00Z")

        cardRepo.reviewCard(cardId, ReviewRating.GOOD, now)

        val card = cardRepo.getCardById(cardId)!!
        assertTrue(card.reviewState.dueAt > now, "Setelah review, kartu dijadwalkan ke masa depan")
    }

    @Test
    fun `countAllDueCards menghitung kartu yang sudah due`() = runTest {
        val dId = ensureDeck()
        // Kartu baru langsung due (dueAt = createdAt = sekarang)
        cardRepo.createCard(dId, "f1", "b1")
        cardRepo.createCard(dId, "f2", "b2")

        val future = Clock.System.now().plus(24, DateTimeUnit.HOUR)
        val dueCount = cardRepo.countAllDueCards(future)
        assertEquals(2L, dueCount)
    }

    @Test
    fun `observeDueCards hanya mengembalikan kartu yang due`() = runTest {
        val dId = ensureDeck()
        val cardId = cardRepo.createCard(dId, "f", "b")
        val now = Instant.parse("2026-05-19T10:00:00Z")

        // Sebelum review: due (dueAt awal = waktu pembuatan, sudah lewat relatif now masa depan)
        val futureNow = Clock.System.now().plus(24, DateTimeUnit.HOUR)
        assertEquals(1, cardRepo.observeDueCards(dId, futureNow).first().size)

        // Setelah review GOOD: dueAt maju 1 hari, jadi tidak due pada `now` yang sama
        cardRepo.reviewCard(cardId, ReviewRating.GOOD, futureNow)
        val due = cardRepo.observeDueCards(dId, futureNow).first()
        assertTrue(due.isEmpty(), "Kartu yang baru di-review tidak due lagi")
    }

    @Test
    fun `menghapus deck cascade menghapus kartunya`() = runTest {
        val dId = ensureDeck()
        cardRepo.createCard(dId, "f1", "b1")
        cardRepo.createCard(dId, "f2", "b2")

        deckRepo.deleteDeck(dId)

        // Cascade: kartu ikut terhapus
        assertTrue(cardRepo.observeCardsByDeck(dId).first().isEmpty())
    }
}
