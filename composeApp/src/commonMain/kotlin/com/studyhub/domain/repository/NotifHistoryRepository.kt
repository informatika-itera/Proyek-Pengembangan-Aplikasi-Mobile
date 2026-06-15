package com.studyhub.domain.repository

import com.studyhub.domain.model.NotifHistoryItem
import com.studyhub.domain.model.NotifType
import kotlinx.coroutines.flow.Flow

interface NotifHistoryRepository {
    suspend fun getHistory(): List<NotifHistoryItem>
    suspend fun getUnreadCount(): Int
    fun observeUnreadCount(): Flow<Int>
    suspend fun addToHistory(
        taskId: String,
        taskTitle: String,
        taskSubject: String,
        aiReason: String,
        type: NotifType = NotifType.TASK
    )
    suspend fun markAllRead()
    suspend fun markRead(id: String)
    suspend fun deleteItem(id: String)
    suspend fun clearAll()
    suspend fun deleteOlderThan(timestamp: Long): Int
    suspend fun deleteExcessItems(max: Int): Int
    suspend fun runAutoCleanupIfNeeded(): CleanupResult
}

data class CleanupResult(val wasRun: Boolean, val deletedCount: Int)
