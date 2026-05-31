package com.studyhub.domain.repository

import com.studyhub.domain.model.Task
import com.studyhub.domain.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface TaskRepository {
    suspend fun addTask(task: Task)
    fun getTaskById(taskId: String): Flow<Task?>
    fun getAllTasks(): Flow<List<Task>>
    fun getActiveTasks(): Flow<List<Task>>
    fun getCompletedTasks(): Flow<List<Task>>
    fun getTasksByDate(date: LocalDate): Flow<List<Task>>
    fun getTasksBySubject(subject: String): Flow<List<Task>>
    suspend fun getOverdueCount(now: Long): Int
    suspend fun getCompletedCountInRange(start: Long, end: Long): Int
    suspend fun updateTask(task: Task)
    suspend fun updateTaskStatus(taskId: String, status: TaskStatus, now: Long)
    suspend fun softDeleteTask(taskId: String, now: Long)
    suspend fun markAsCompleted(taskId: String, now: Long)
}
