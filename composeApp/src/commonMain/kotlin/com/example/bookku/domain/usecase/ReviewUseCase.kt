package com.example.bookku.domain.usecase

import com.example.bookku.domain.model.Review
import com.example.bookku.domain.repository.ReviewRepository

class AddReviewUseCase(private val reviewRepository: ReviewRepository) {
    suspend operator fun invoke(review: Review): Result<Long> {
        if (review.reviewText.isBlank()) return Result.failure(IllegalArgumentException("Review tidak boleh kosong"))
        return reviewRepository.addOrUpdateReview(review)
    }
}

class DeleteReviewUseCase(private val reviewRepository: ReviewRepository) {
    suspend operator fun invoke(reviewId: Long): Result<Unit> = reviewRepository.deleteReview(reviewId)
}
