package com.studyhub.data.local

import com.studyhub.database.StudyHubDatabase
import com.studyhub.database.TaskEntity
import com.studyhub.domain.model.Task
import com.studyhub.domain.model.Priority
import com.studyhub.domain.model.TaskStatus

class LocalTaskDataSource(private val database: StudyHubDatabase) {

    fun insertTask(task: Task) {
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
            updatedAt = task.updatedAt
        )
    }

    fun selectAllTasks(): List<Task> =
        database.taskEntityQueries.selectAllTasks()
            .executeAsList().map { it.toTask() }

    fun selectActiveTasks(): List<Task> =
        database.taskEntityQueries.selectActiveTasks()
            .executeAsList().map { it.toTask() }

    fun selectCompletedTasks(): List<Task> =
        database.taskEntityQueries.selectCompletedTasks()
            .executeAsList().map { it.toTask() }

    fun selectById(taskId: String): Task? =
        database.taskEntityQueries.selectById(taskId)
            .executeAsOneOrNull()?.toTask()

    fun selectByDate(start: Long, end: Long): List<Task> =
        database.taskEntityQueries.selectByDate(start, end)
            .executeAsList().map { it.toTask() }

    fun selectBySubject(subject: String): List<Task> =
        database.taskEntityQueries.selectBySubject(subject)
            .executeAsList().map { it.toTask() }

    fun selectOverdueCount(now: Long): Long =
        database.taskEntityQueries.selectOverdueCount(now)
            .executeAsOne()

    fun selectCompletedCountInRange(start: Long, end: Long): Long =
        database.taskEntityQueries.selectCompletedCountInRange(start, end)
            .executeAsOne()

    fun updateTask(task: Task) {
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
            id = task.id
        )
    }

    fun updateStatus(taskId: String, status: String, now: Long, completedAt: Long?) {
        database.taskEntityQueries.updateStatus(status, now, completedAt, taskId)
    }

    fun softDelete(taskId: String, now: Long) {
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
        updatedAt = updatedAt
    )
}
