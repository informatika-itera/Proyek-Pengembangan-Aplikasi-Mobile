package com.example.neurodeck.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.neurodeck.data.local.NeuroDeckDatabase
import com.example.neurodeck.data.local.mapper.toDomain
import com.example.neurodeck.domain.model.Card
import com.example.neurodeck.domain.model.ReviewRating
import com.example.neurodeck.domain.repository.CardRepository
import com.example.neurodeck.domain.usecase.CalculateNextReviewUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * SQLDelight-backed implementation of [CardRepository].
 *
 * Method paling kompleks adalah [reviewCard]: composite operation yang
 * (1) baca state lama, (2) hitung state baru via SM-2 use case,
 * (3) update tabel cards, (4) insert ke tabel review_records.
 * Semua dibungkus transaction untuk atomicity.
 */
class CardRepositoryImpl(
    database: NeuroDeckDatabase,
    private val calculateNextReview: CalculateNextReviewUseCase,
) : CardRepository {

    private val cardQueries = database.cardQueries
    private val reviewRecordQueries = database.reviewRecordQueries

    // ==================== READ OPERATIONS ====================

    override fun observeCardsByDeck(deckId: Long): Flow<List<Card>> =
        cardQueries.selectByDeck(deckId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows -> rows.map { it.toDomain() } }

    override suspend fun getCardById(cardId: Long): Card? =
        withContext(Dispatchers.IO) {
            cardQueries.selectById(cardId).executeAsOneOrNull()?.toDomain()
        }

    override fun observeDueCards(deckId: Long, now: Instant): Flow<List<Card>> =
        cardQueries.selectDueByDeck(deckId, now.toEpochMilliseconds())
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows -> rows.map { it.toDomain() } }

    override suspend fun countAllDueCards(now: Instant): Long =
        withContext(Dispatchers.IO) {
            cardQueries.countAllDue(now.toEpochMilliseconds()).executeAsOne()
        }

    // ==================== WRITE OPERATIONS — CRUD ====================

    override suspend fun createCard(deckId: Long, front: String, back: String): Long =
        withContext(Dispatchers.IO) {
            val now = Clock.System.now().toEpochMilliseconds()
            cardQueries.transactionWithResult {
                cardQueries.insert(
                    deckId = deckId,
                    front = front,
                    back = back,
                    easeFactor = 2.5,           // SM-2 default untuk kartu baru
                    intervalDays = 0,
                    repetitions = 0,
                    lastReviewedAt = null,
                    dueAt = now,                // langsung due saat dibuat
                    createdAt = now,
                )
                cardQueries.lastInsertedId().executeAsOne()
            }
        }

    /**
     * Bulk insert. Untuk Fase E (AI-generated cards) — sekali generate bisa 20+ kartu.
     * Dibungkus 1 transaction supaya: (1) atomic — kalau gagal di tengah, semua rollback,
     * (2) performance — 1 transaction lebih cepat dari 20 transaction individual.
     */
    override suspend fun createCards(
        deckId: Long,
        cards: List<Pair<String, String>>,
    ): List<Long> = withContext(Dispatchers.IO) {
        val now = Clock.System.now().toEpochMilliseconds()
        val insertedIds = mutableListOf<Long>()

        cardQueries.transaction {
            for ((front, back) in cards) {
                cardQueries.insert(
                    deckId = deckId,
                    front = front,
                    back = back,
                    easeFactor = 2.5,
                    intervalDays = 0,
                    repetitions = 0,
                    lastReviewedAt = null,
                    dueAt = now,
                    createdAt = now,
                )
                insertedIds += cardQueries.lastInsertedId().executeAsOne()
            }
        }
        insertedIds
    }

    override suspend fun updateCardContent(id: Long, front: String, back: String) {
        withContext(Dispatchers.IO) {
            cardQueries.updateContent(front = front, back = back, id = id)
        }
    }

    override suspend fun deleteCard(id: Long) {
        withContext(Dispatchers.IO) {
            cardQueries.deleteById(id)
        }
    }

    // ==================== STUDY SESSION OPERATION ====================

    /**
     * Apply user rating di Study Session — INTI dari SM-2 spaced repetition.
     *
     * Algoritma:
     * 1. Load state kartu sekarang dari DB (easeFactor, intervalDays, repetitions, dst)
     * 2. Pakai [CalculateNextReviewUseCase] untuk hitung state baru berdasarkan rating
     * 3. Persist state baru ke tabel cards
     * 4. Log entry ke tabel review_records untuk Statistics & history
     *
     * Step 3 & 4 dibungkus transaction. Kalau step 3 sukses tapi step 4 crash,
     * tanpa transaction kita dapat: state baru ter-save tapi review tidak ter-log
     * (inconsistency). Dengan transaction: kalau salah satu gagal, rollback semua.
     */
    override suspend fun reviewCard(cardId: Long, rating: ReviewRating, now: Instant) {
        withContext(Dispatchers.IO) {
            // Step 1: load state lama
            val currentEntity = cardQueries.selectById(cardId).executeAsOneOrNull()
                ?: error("Card with id=$cardId not found")

            val currentCard = currentEntity.toDomain()

            // Step 2: hitung state baru (pure function, di luar transaction)
            val newReviewState = calculateNextReview(
                current = currentCard.reviewState,
                rating = rating,
                now = now,
            )

            // Step 3 & 4: persist atomically
            cardQueries.transaction {
                cardQueries.updateReviewState(
                    easeFactor = newReviewState.easeFactor,
                    intervalDays = newReviewState.intervalDays.toLong(),
                    repetitions = newReviewState.repetitions.toLong(),
                    lastReviewedAt = newReviewState.lastReviewedAt?.toEpochMilliseconds(),
                    dueAt = newReviewState.dueAt.toEpochMilliseconds(),
                    id = cardId,
                )
                reviewRecordQueries.insert(
                    cardId = cardId,
                    rating = rating.quality.toLong(),
                    reviewedAt = now.toEpochMilliseconds(),
                )
            }
        }
    }
}