package com.example.neurodeck.domain.repository

import com.example.neurodeck.domain.model.Card
import com.example.neurodeck.domain.model.ReviewRating
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

/**
 * Kontrak untuk operasi CRUD card + review state management.
 *
 * Method [reviewCard] adalah operasi compound:
 * (1) baca state lama, (2) hitung state baru via SM-2 use case,
 * (3) persist state baru, (4) log ReviewRecord.
 * Atomicity di-handle implementor (gunakan transaction).
 */
interface CardRepository {

    /** Stream semua card di deck. */
    fun observeCardsByDeck(deckId: Long): Flow<List<Card>>

    /** Stream kartu yang due untuk review di deck tertentu. */
    fun observeDueCards(deckId: Long, now: Instant): Flow<List<Card>>

    /** Ambil 1 card sekali (snapshot, bukan flow). Untuk load form Edit. */
    suspend fun getCardById(cardId: Long): Card?

    /** Hitung jumlah kartu due across all decks (untuk Statistics). */
    suspend fun countAllDueCards(now: Instant): Long

    suspend fun createCard(deckId: Long, front: String, back: String): Long

    /** Bulk insert (untuk AI-generated cards di Fase E). */
    suspend fun createCards(deckId: Long, cards: List<Pair<String, String>>): List<Long>

    suspend fun updateCardContent(id: Long, front: String, back: String)

    suspend fun deleteCard(id: Long)

    /**
     * Apply user rating untuk satu kartu di Study Session.
     * Update SM-2 state + log review record dalam 1 transaction.
     */
    suspend fun reviewCard(cardId: Long, rating: ReviewRating, now: Instant)
}