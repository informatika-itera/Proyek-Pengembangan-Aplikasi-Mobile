package com.example.neurodeck.domain.model

import kotlinx.datetime.Instant

/**
 * Record satu kali review di Study Session.
 * Disimpan ke tabel `review_records` untuk Statistics.
 *
 * Berbeda dengan [CardReviewState] yang represent "state saat ini" sebuah kartu,
 * [ReviewRecord] adalah "snapshot historis": user pernah review kartu X
 * di waktu Y dengan rating Z.
 */
data class ReviewRecord(
    val id: Long = 0,
    val cardId: Long,
    val rating: ReviewRating,
    val reviewedAt: Instant,
)