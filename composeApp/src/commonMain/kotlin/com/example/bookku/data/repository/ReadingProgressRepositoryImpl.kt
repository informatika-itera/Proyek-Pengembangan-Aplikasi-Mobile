package com.example.bookku.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.bookku.data.local.BookDatabase
import com.example.bookku.data.local.entity.toDomain
import com.example.bookku.domain.model.ReadingProgress
import com.example.bookku.domain.model.ReadingStatus
import com.example.bookku.domain.repository.ReadingProgressRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class ReadingProgressRepositoryImpl(private val database: BookDatabase) : ReadingProgressRepository {

    private val queries = database.readingProgressQueries

    override fun getReadingProgress(userId: String, bookId: Long): Flow<ReadingProgress?> {
        return queries.getReadingProgress(userId, bookId)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toDomain() }
    }

    override fun getAllReadingProgress(userId: String): Flow<List<ReadingProgress>> {
        return queries.getAllReadingProgress(userId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun updateProgress(userId: String, bookId: Long, currentPage: Int, status: ReadingStatus): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val finishedAt = if (status == ReadingStatus.COMPLETED) Clock.System.now().toEpochMilliseconds() else null
            queries.updateReadingProgress(
                current_page = currentPage.toLong(),
                status = status.name,
                finished_at = finishedAt,
                notes = "",
                user_id = userId,
                book_id = bookId
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addReadingProgress(
        userId: String,
        progress: ReadingProgress
    ): Result<Long> = withContext(Dispatchers.IO) {
        try {
            queries.addReadingProgress(
                user_id = userId,
                book_id = progress.bookId,
                current_page = progress.currentPage.toLong(),
                total_pages = progress.totalPages.toLong(),
                status = progress.status.name,
                started_at = progress.startedAt.toEpochMilliseconds(),
                finished_at = progress.finishedAt?.toEpochMilliseconds(),
                notes = progress.notes
            )
            Result.success(progress.bookId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun completeReading(userId: String, bookId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            queries.markAsCompleted(
                finished_at = Clock.System.now().toEpochMilliseconds(),
                user_id = userId,
                book_id = bookId
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getReadingStats(userId: String): Flow<Map<ReadingStatus, Int>> {
        return queries.getReadingStats(userId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.associate { ReadingStatus.valueOf(it.status) to it.total.toInt() }
            }
    }
}