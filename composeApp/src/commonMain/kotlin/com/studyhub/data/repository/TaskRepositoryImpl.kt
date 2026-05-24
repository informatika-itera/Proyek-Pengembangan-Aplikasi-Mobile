package com.studyhub.data.repository

import com.studyhub.core.util.atEndOfDayMillis
import com.studyhub.core.util.atStartOfDayMillis
import com.studyhub.data.local.LocalTaskDataSource
import com.studyhub.domain.model.Task
import com.studyhub.domain.model.TaskStatus
import com.studyhub.domain.repository.TaskRepository
import kotlinx.datetime.LocalDate

class TaskRepositoryImpl(
    private val localDataSource: LocalTaskDataSource
) : TaskRepository {
    override suspend fun addTask(task: Task) = localDataSource.insertTask(task)
    override suspend fun getTaskById(taskId: String) = localDataSource.selectById(taskId)
    override suspend fun getAllTasks() = localDataSource.selectAllTasks()
    override suspend fun getActiveTasks() = localDataSource.selectActiveTasks()
    override suspend fun getCompletedTasks() = localDataSource.selectCompletedTasks()
    override suspend fun getTasksByDate(date: LocalDate) =
        localDataSource.selectByDate(date.atStartOfDayMillis(), date.atEndOfDayMillis() + 1)
    override suspend fun getTasksBySubject(subject: String) =
        localDataSource.selectBySubject(subject)
    override suspend fun getOverdueCount(now: Long) =
        localDataSource.selectOverdueCount(now).toInt()
    override suspend fun getCompletedCountInRange(start: Long, end: Long) =
        localDataSource.selectCompletedCountInRange(start, end).toInt()
    override suspend fun updateTask(task: Task) = localDataSource.updateTask(task)
    override suspend fun updateTaskStatus(taskId: String, status: TaskStatus, now: Long) =
        localDataSource.updateStatus(
            taskId, status.value, now,
            if (status == TaskStatus.DONE) now else null
        )
    override suspend fun softDeleteTask(taskId: String, now: Long) =
        localDataSource.softDelete(taskId, now)
    override suspend fun markAsCompleted(taskId: String, now: Long) =
        localDataSource.updateStatus(taskId, TaskStatus.DONE.value, now, now)
}
