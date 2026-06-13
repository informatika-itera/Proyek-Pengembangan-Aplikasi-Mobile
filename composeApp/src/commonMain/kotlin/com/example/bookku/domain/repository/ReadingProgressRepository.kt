package com.example.bookku.domain.repository

import com.example.bookku.domain.model.ReadingProgress
import com.example.bookku.domain.model.ReadingStatus
import kotlinx.coroutines.flow.Flow

interface ReadingProgressRepository {
    fun getReadingProgress(userId: String, bookId: Long): Flow<ReadingProgress?>
    fun getAllReadingProgress(userId: String): Flow<List<ReadingProgress>>
    suspend fun updateProgress(userId: String, bookId: Long, currentPage: Int, status: ReadingStatus): Result<Unit>
    suspend fun addReadingProgress(userId: String, progress: ReadingProgress): Result<Long>
    suspend fun completeReading(userId: String, bookId: Long): Result<Unit>
    fun getReadingStats(userId: String): Flow<Map<ReadingStatus, Int>>
}
