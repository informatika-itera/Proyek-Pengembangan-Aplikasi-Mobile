package com.studyhub.domain.repository

import com.studyhub.domain.model.NotifHistoryItem

interface NotifHistoryRepository {
    suspend fun getHistory(): List<NotifHistoryItem>
    suspend fun getUnreadCount(): Int
    suspend fun addToHistory(
        taskId: String,
        taskTitle: String,
        taskSubject: String,
        aiReason: String
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
