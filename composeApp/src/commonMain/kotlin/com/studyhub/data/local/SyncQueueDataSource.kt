package com.studyhub.data.local

import com.studyhub.database.StudyHubDatabase
import com.studyhub.database.SyncQueueEntity
import com.studyhub.core.util.currentTimeMillis
import com.studyhub.core.util.uuid

enum class SyncOperation {
    CREATE, UPDATE, DELETE, STATUS_UPDATE
}

data class SyncQueueItem(
    val id: String,
    val operation: SyncOperation,
    val taskId: String,
    val payload: String,
    val createdAt: Long,
    val retryCount: Int
)

class SyncQueueDataSource(
    private val database: StudyHubDatabase
) {
    fun getAllPending(): List<SyncQueueItem> =
        database.syncQueueEntityQueries
            .selectAll()
            .executeAsList()
            .map { it.toItem() }

    fun enqueue(
        taskId: String,
        operation: SyncOperation,
        payload: String
    ) {
        database.syncQueueEntityQueries.insert(
            id = uuid(),
            operation = operation.name,
            taskId = taskId,
            payload = payload,
            createdAt = currentTimeMillis(),
            retryCount = 0
        )
    }

    fun remove(id: String) {
        database.syncQueueEntityQueries.deleteById(id)
    }

    fun incrementRetry(id: String) {
        database.syncQueueEntityQueries.incrementRetry(id)
    }

    fun cleanupFailed() {
        database.syncQueueEntityQueries.deleteOldFailed()
    }

    private fun SyncQueueEntity.toItem() = SyncQueueItem(
        id = id,
        operation = SyncOperation.valueOf(operation),
        taskId = taskId,
        payload = payload,
        createdAt = createdAt,
        retryCount = retryCount.toInt()
    )
}
