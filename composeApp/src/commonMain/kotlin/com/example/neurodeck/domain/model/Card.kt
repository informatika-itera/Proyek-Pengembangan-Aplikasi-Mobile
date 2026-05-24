package com.example.neurodeck.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Card = 1 flashcard. Punya front (pertanyaan) dan back (jawaban).
 * State SM-2 di-embed sebagai [reviewState] supaya domain logic
 * (CalculateNextReviewUseCase) bisa langsung consume.
 *
 * Catatan: [Card] dan [CardReviewState] sengaja dipisah supaya
 * SM-2 logic tidak tahu tentang field UI (front, back, deckId).
 */
data class Card(
    val id: Long = 0,
    val deckId: Long,
    val front: String,
    val back: String,
    val reviewState: CardReviewState = CardReviewState.initial,
    val createdAt: Instant = Clock.System.now(),
)