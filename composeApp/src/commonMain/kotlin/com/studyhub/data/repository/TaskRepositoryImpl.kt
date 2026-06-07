package com.studyhub.data.repository

import com.studyhub.core.util.atEndOfDayMillis
import com.studyhub.core.util.atStartOfDayMillis
import com.studyhub.data.local.LocalTaskDataSource
import com.studyhub.data.local.SyncOperation
import com.studyhub.data.local.SyncQueueDataSource
import com.studyhub.core.util.NetworkMonitor
import com.studyhub.domain.model.Task
import com.studyhub.domain.model.TaskStatus
import com.studyhub.domain.model.toAiSummary
import com.studyhub.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TaskRepositoryImpl(
    private val localDataSource: LocalTaskDataSource,
    private val syncQueueDataSource: SyncQueueDataSource,
    private val networkMonitor: NetworkMonitor
) : TaskRepository {

    private val isOnline: Flow<Boolean> = networkMonitor.isOnline

    override suspend fun addTask(task: Task) {
        // Selalu simpan lokal dulu
        localDataSource.insertTask(task)

        // Enqueue untuk sync jika ada koneksi nanti
        val payload = Json.encodeToString(task.toAiSummary())
        syncQueueDataSource.enqueue(
            taskId = task.id,
            operation = SyncOperation.CREATE,
            payload = payload
        )
    }

    override fun getTaskById(taskId: String) = localDataSource.selectById(taskId)
    override fun getAllTasks() = localDataSource.selectAllTasks()
    override fun getActiveTasks() = localDataSource.selectActiveTasks()
    override fun getCompletedTasks() = localDataSource.selectCompletedTasks()
    override fun getTasksByDate(date: LocalDate) =
        localDataSource.selectByDate(date.atStartOfDayMillis(), date.atEndOfDayMillis() + 1)
    override fun getTasksBySubject(subject: String) =
        localDataSource.selectBySubject(subject)
    override suspend fun getOverdueCount(now: Long) =
        localDataSource.selectOverdueCount(now).toInt()
    override suspend fun getCompletedCountInRange(start: Long, end: Long) =
        localDataSource.selectCompletedCountInRange(start, end).toInt()

    override suspend fun updateTask(task: Task) {
        localDataSource.updateTask(task)
        syncQueueDataSource.enqueue(
            taskId = task.id,
            operation = SyncOperation.UPDATE,
            payload = Json.encodeToString(task.toAiSummary())
        )
    }

    override suspend fun updateTaskStatus(taskId: String, status: TaskStatus, now: Long) {
        localDataSource.updateStatus(
            taskId, status.value, now,
            if (status == TaskStatus.DONE) now else null
        )
        syncQueueDataSource.enqueue(
            taskId = taskId,
            operation = SyncOperation.STATUS_UPDATE,
            payload = status.value
        )
    }

    override suspend fun softDeleteTask(taskId: String, now: Long) {
        localDataSource.softDelete(taskId, now)
        syncQueueDataSource.enqueue(
            taskId = taskId,
            operation = SyncOperation.DELETE,
            payload = taskId
        )
    }

    override suspend fun markAsCompleted(taskId: String, now: Long) {
        localDataSource.updateStatus(taskId, TaskStatus.DONE.value, now, now)
        syncQueueDataSource.enqueue(
            taskId = taskId,
            operation = SyncOperation.STATUS_UPDATE,
            payload = TaskStatus.DONE.value
        )
    }
}
