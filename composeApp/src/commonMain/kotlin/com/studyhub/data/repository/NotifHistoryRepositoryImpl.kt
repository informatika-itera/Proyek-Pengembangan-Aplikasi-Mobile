package com.studyhub.data.repository

import com.studyhub.data.local.NotifHistoryDataSource
import com.studyhub.data.local.PreferencesDataSource
import com.studyhub.domain.model.NotifType
import com.studyhub.domain.repository.CleanupResult
import com.studyhub.domain.repository.NotifHistoryRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock

class NotifHistoryRepositoryImpl(
    private val dataSource: NotifHistoryDataSource,
    private val preferencesDataSource: PreferencesDataSource
) : NotifHistoryRepository {
    override suspend fun getHistory() = dataSource.getAll()
    override suspend fun getUnreadCount() = dataSource.getUnreadCount()
    override fun observeUnreadCount() = dataSource.observeUnreadCount()
    
    override suspend fun addToHistory(
        taskId: String, taskTitle: String,
        taskSubject: String, aiReason: String,
        type: NotifType
    ) {
        dataSource.insert(taskId, taskTitle, taskSubject, aiReason, type)
        runAutoCleanupIfNeeded()
    }

    override suspend fun markAllRead() = dataSource.markAllRead()
    override suspend fun markRead(id: String) = dataSource.markRead(id)
    override suspend fun deleteItem(id: String) = dataSource.deleteById(id)
    override suspend fun clearAll() = dataSource.deleteAll()

    override suspend fun deleteOlderThan(timestamp: Long): Int {
        val countBefore = dataSource.getCount()
        dataSource.deleteOlderThan(timestamp)
        return countBefore - dataSource.getCount()
    }

    override suspend fun deleteExcessItems(max: Int): Int {
        val countBefore = dataSource.getCount()
        dataSource.deleteExcessItems(max)
        return countBefore - dataSource.getCount()
    }

    override suspend fun runAutoCleanupIfNeeded(): CleanupResult {
        val enabled = preferencesDataSource.notifAutoDeleteEnabled.first()
        if (!enabled) return CleanupResult(false, 0)

        val lastCleanup = preferencesDataSource.getLastNotifCleanup()
        val now = Clock.System.now().toEpochMilliseconds()
        
        // Run cleanup every 24 hours
        if (now - lastCleanup < 86_400_000L) {
            return CleanupResult(false, 0)
        }

        val maxCount = preferencesDataSource.notifMaxHistoryCount.first()
        var deleted = deleteExcessItems(maxCount)
        
        // Also delete older than 7 days anyway to keep DB small
        deleted += deleteOlderThan(now - (7 * 86_400_000L))

        preferencesDataSource.setLastNotifCleanup(now)
        return CleanupResult(true, deleted)
    }
}
