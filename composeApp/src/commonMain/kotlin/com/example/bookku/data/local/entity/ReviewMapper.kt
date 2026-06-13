package com.example.bookku.data.local.entity

import com.example.bookku.data.local.Review
import com.example.bookku.domain.model.Review as ReviewDomain
import kotlinx.datetime.Instant

fun Review.toDomain(): ReviewDomain {
    return ReviewDomain(
        id = id,
        bookId = book_id,
        userId = user_id,
        userName = user_name,
        rating = (rating ?: 0.0).toFloat(),
        reviewText = review_text ?: "",
        createdAt = Instant.fromEpochMilliseconds(created_at),
        updatedAt = Instant.fromEpochMilliseconds(updated_at)
    )
}

fun List<Review>.toDomainList(): List<ReviewDomain> {
    return map { it.toDomain() }
}