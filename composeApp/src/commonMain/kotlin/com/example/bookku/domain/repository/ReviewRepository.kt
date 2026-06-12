package com.example.bookku.domain.repository

import com.example.bookku.domain.model.Review
import com.example.bookku.domain.model.RatingStats
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {
    fun getReviewsByBook(bookId: Long): Flow<List<Review>>
    fun getRatingStats(bookId: Long): Flow<RatingStats>
    fun getUserReview(bookId: Long, userId: String): Flow<Review?>
    suspend fun addOrUpdateReview(review: Review): Result<Long>
    suspend fun deleteReview(reviewId: Long): Result<Unit>
    fun getAllReviews(): Flow<List<Review>>
}
