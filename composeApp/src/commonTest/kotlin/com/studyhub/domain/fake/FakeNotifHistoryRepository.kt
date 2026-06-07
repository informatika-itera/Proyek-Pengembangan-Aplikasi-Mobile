package com.studyhub.domain.fake

import com.studyhub.domain.model.NotifHistoryItem
import com.studyhub.domain.repository.CleanupResult
import com.studyhub.domain.repository.NotifHistoryRepository

class FakeNotifHistoryRepository : NotifHistoryRepository {
    val items = mutableListOf<NotifHistoryItem>()

    override suspend fun getHistory(): List<NotifHistoryItem> = items
    override suspend fun getUnreadCount(): Int = items.count { !it.isRead }
    
    override suspend fun addToHistory(taskId: String, taskTitle: String, taskSubject: String, aiReason: String) {
        items.add(NotifHistoryItem("id", taskId, taskTitle, taskSubject, aiReason, 0L, false))
    }

    override suspend fun markAllRead() { items.forEach { /* no-op */ } }
    override suspend fun markRead(id: String) { /* no-op */ }
    override suspend fun deleteItem(id: String) { items.removeAll { it.id == id } }
    override suspend fun clearAll() { items.clear() }
    override suspend fun deleteOlderThan(timestamp: Long): Int = 0
    override suspend fun deleteExcessItems(max: Int): Int = 0
    override suspend fun runAutoCleanupIfNeeded(): CleanupResult = CleanupResult(false, 0)
}
