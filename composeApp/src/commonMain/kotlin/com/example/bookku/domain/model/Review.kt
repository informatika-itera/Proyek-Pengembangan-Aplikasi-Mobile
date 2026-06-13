package com.example.bookku.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Review(
    val id: Long = 0,
    val bookId: Long,
    val userId: String,
    val userName: String,
    val rating: Float,
    val reviewText: String = "",
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now()
) {
    init {
        require(rating in 1f..5f) { "Rating harus antara 1-5" }
    }
}

data class RatingStats(
    val averageRating: Float = 0f,
    val totalReviews: Int = 0,
    val distribution: Map<Int, Int> = emptyMap()
)
