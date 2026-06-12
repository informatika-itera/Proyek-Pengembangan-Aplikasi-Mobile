package com.example.bookku.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.bookku.data.local.BookDatabase
import com.example.bookku.data.local.entity.toDomain
import com.example.bookku.domain.model.Review
import com.example.bookku.domain.model.RatingStats
import com.example.bookku.domain.repository.ReviewRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlin.math.roundToInt

class ReviewRepositoryImpl(private val database: BookDatabase) : ReviewRepository {

    private val queries = database.reviewQueries

    override fun getReviewsByBook(bookId: Long): Flow<List<Review>> {
        return queries.getReviewsByBook(bookId)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getRatingStats(bookId: Long): Flow<RatingStats> {
        return queries.getReviewsByBook(bookId)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities ->
                val reviews = entities.map { it.toDomain() }
                if (reviews.isEmpty()) {
                    RatingStats()
                } else {
                    val avg = reviews.map { it.rating }.average().toFloat()
                    // Round to 1 decimal place without String.format (KMP friendly)
                    val roundedAvg = (avg * 10).roundToInt() / 10f
                    
                    RatingStats(
                        averageRating = roundedAvg,
                        totalReviews = reviews.size,
                        distribution = reviews.groupingBy { it.rating.toInt() }.eachCount()
                    )
                }
            }
    }

    override fun getUserReview(bookId: Long, userId: String): Flow<Review?> {
        return queries.getUserReview(bookId, userId)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { it?.toDomain() }
    }

    override suspend fun addOrUpdateReview(review: Review): Result<Long> = withContext(Dispatchers.Default) {
        try {
            queries.insertOrUpdateReview(
                book_id = review.bookId,
                user_id = review.userId,
                user_name = review.userName,
                rating = review.rating.toDouble(),
                review_text = review.reviewText,
                created_at = review.createdAt.toEpochMilliseconds(),
                updated_at = Clock.System.now().toEpochMilliseconds()
            )
            Result.success(review.bookId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteReview(reviewId: Long): Result<Unit> = withContext(Dispatchers.Default) {
        try {
            queries.deleteReview(reviewId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getAllReviews(): Flow<List<Review>> {
        return queries.getAllReviews()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toDomain() } }
    }
}
