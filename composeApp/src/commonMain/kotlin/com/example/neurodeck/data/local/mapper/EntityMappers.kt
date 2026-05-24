package com.example.neurodeck.data.local.mapper

import com.example.neurodeck.data.local.CardEntity
import com.example.neurodeck.data.local.DeckEntity
import com.example.neurodeck.data.local.ReviewRecordEntity
import com.example.neurodeck.data.local.SelectAllWithCardCount
import com.example.neurodeck.domain.model.Card
import com.example.neurodeck.domain.model.CardReviewState
import com.example.neurodeck.domain.model.Deck
import com.example.neurodeck.domain.model.ReviewRating
import com.example.neurodeck.domain.model.ReviewRecord
import kotlinx.datetime.Instant

// ==================== DECK MAPPERS ====================

/**
 * DeckEntity → Deck domain model.
 *
 * Note: [cardCount] di-set ke 0 karena DeckEntity sendiri tidak punya field ini.
 * Untuk query yang butuh count, pakai [SelectAllWithCardCount.toDomain] di bawah.
 */
fun DeckEntity.toDomain(): Deck = Deck(
    id = id,
    title = title,
    description = description,
    cardCount = 0,
    createdAt = Instant.fromEpochMilliseconds(createdAt),
    updatedAt = Instant.fromEpochMilliseconds(updatedAt),
)

/**
 * Generated result type dari query `selectAllWithCardCount`.
 * SQLDelight generate class baru karena return type-nya bukan single-table row.
 */
fun SelectAllWithCardCount.toDomain(): Deck = Deck(
    id = id,
    title = title,
    description = description,
    cardCount = cardCount.toInt(),
    createdAt = Instant.fromEpochMilliseconds(createdAt),
    updatedAt = Instant.fromEpochMilliseconds(updatedAt),
)

// ==================== CARD MAPPERS ====================

/**
 * CardEntity → Card domain model.
 *
 * SM-2 state di-construct dari multiple field jadi satu CardReviewState.
 * Ini contoh kenapa mapper berguna: data layer flat (5 field SM-2 separate),
 * domain layer encapsulated (1 nested object).
 */
fun CardEntity.toDomain(): Card = Card(
    id = id,
    deckId = deckId,
    front = front,
    back = back,
    reviewState = CardReviewState(
        easeFactor = easeFactor,
        intervalDays = intervalDays.toInt(),
        repetitions = repetitions.toInt(),
        lastReviewedAt = lastReviewedAt?.let(Instant::fromEpochMilliseconds),
        dueAt = Instant.fromEpochMilliseconds(dueAt),
    ),
    createdAt = Instant.fromEpochMilliseconds(createdAt),
)

// ==================== REVIEW RECORD MAPPERS ====================

fun ReviewRecordEntity.toDomain(): ReviewRecord = ReviewRecord(
    id = id,
    cardId = cardId,
    rating = ReviewRating.fromQuality(rating.toInt()),
    reviewedAt = Instant.fromEpochMilliseconds(reviewedAt),
)

/**
 * Inverse mapping: dari quality score (DB) ke enum (domain).
 *
 * Defensive: kalau di DB ada quality value yang tidak match enum
 * (corruption / future migration), fallback ke AGAIN supaya kartu
 * di-treat sebagai "tidak hafal" — itu pilihan aman daripada crash.
 */
private fun ReviewRating.Companion.fromQuality(quality: Int): ReviewRating =
    ReviewRating.entries.firstOrNull { it.quality == quality } ?: ReviewRating.AGAIN