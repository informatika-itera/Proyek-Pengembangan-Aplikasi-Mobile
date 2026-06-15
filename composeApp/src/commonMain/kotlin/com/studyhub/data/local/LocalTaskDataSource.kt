package com.studyhub.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.studyhub.database.StudyHubDatabase
import com.studyhub.database.TaskEntity
import com.studyhub.domain.model.Task
import com.studyhub.domain.model.Priority
import com.studyhub.domain.model.TaskStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class LocalTaskDataSource(private val database: StudyHubDatabase) {

    suspend fun insertTask(task: Task) = withContext(Dispatchers.IO) {
        database.taskEntityQueries.insertTask(
            id = task.id,
            title = task.title,
            description = task.description,
            subject = task.subject,
            priority = task.priority.name.lowercase(),
            status = task.status.value,
            dueDate = task.dueDate,
            dueTime = task.dueTime,
            tags = task.tags.joinToString(",", "[", "]") { "\"$it\"" },
            estimatedMinutes = task.estimatedMinutes.toLong(),
            isDeleted = if (task.isDeleted) 1L else 0L,
            completedAt = task.completedAt,
            createdAt = task.createdAt,
            updatedAt = task.updatedAt,
            colorHex = task.colorHex
        )
    }

    fun selectAllTasks(): Flow<List<Task>> =
        database.taskEntityQueries.selectAllTasks()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toTask() } }

    fun selectActiveTasks(): Flow<List<Task>> =
        database.taskEntityQueries.selectActiveTasks()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toTask() } }

    fun selectCompletedTasks(): Flow<List<Task>> =
        database.taskEntityQueries.selectCompletedTasks()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toTask() } }

    fun selectById(taskId: String): Flow<Task?> =
        database.taskEntityQueries.selectById(taskId)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toTask() }

    fun selectByDate(start: Long, end: Long): Flow<List<Task>> =
        database.taskEntityQueries.selectByDate(start, end)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toTask() } }

    fun selectBySubject(subject: String): Flow<List<Task>> =
        database.taskEntityQueries.selectBySubject(subject)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toTask() } }

    fun selectOverdueCount(now: Long): Long =
        database.taskEntityQueries.selectOverdueCount(now)
            .executeAsOne()

    fun selectCompletedCountInRange(start: Long, end: Long): Long =
        database.taskEntityQueries.selectCompletedCountInRange(start, end)
            .executeAsOne()

    suspend fun updateTask(task: Task) = withContext(Dispatchers.IO) {
        database.taskEntityQueries.updateTask(
            title = task.title,
            description = task.description,
            subject = task.subject,
            priority = task.priority.name.lowercase(),
            status = task.status.value,
            dueDate = task.dueDate,
            dueTime = task.dueTime,
            tags = task.tags.joinToString(",", "[", "]") { "\"$it\"" },
            estimatedMinutes = task.estimatedMinutes.toLong(),
            updatedAt = task.updatedAt,
            colorHex = task.colorHex,
            id = task.id
        )
    }

    suspend fun updateStatus(taskId: String, status: String, now: Long, completedAt: Long?) = withContext(Dispatchers.IO) {
        database.taskEntityQueries.updateStatus(status, now, completedAt, taskId)
    }

    suspend fun softDelete(taskId: String, now: Long) = withContext(Dispatchers.IO) {
        database.taskEntityQueries.softDelete(now, taskId)
    }

    private fun TaskEntity.toTask(): Task = Task(
        id = id,
        title = title,
        description = description,
        subject = subject,
        priority = Priority.valueOf(priority.uppercase()),
        status = TaskStatus.fromString(status),
        dueDate = dueDate,
        dueTime = dueTime,
        tags = tags.removeSurrounding("[", "]")
            .split(",")
            .map { it.trim().removeSurrounding("\"") }
            .filter { it.isNotBlank() },
        estimatedMinutes = estimatedMinutes.toInt(),
        isDeleted = isDeleted == 1L,
        completedAt = completedAt,
        createdAt = createdAt,
        updatedAt = updatedAt,
        colorHex = colorHex
    )
}
