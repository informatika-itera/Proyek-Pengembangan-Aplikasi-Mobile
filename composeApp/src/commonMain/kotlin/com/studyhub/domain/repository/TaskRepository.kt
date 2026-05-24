package com.studyhub.domain.repository

import com.studyhub.domain.model.Task
import com.studyhub.domain.model.TaskStatus
import kotlinx.datetime.LocalDate

interface TaskRepository {
    suspend fun addTask(task: Task)
    suspend fun getTaskById(taskId: String): Task?
    suspend fun getAllTasks(): List<Task>
    suspend fun getActiveTasks(): List<Task>
    suspend fun getCompletedTasks(): List<Task>
    suspend fun getTasksByDate(date: LocalDate): List<Task>
    suspend fun getTasksBySubject(subject: String): List<Task>
    suspend fun getOverdueCount(now: Long): Int
    suspend fun getCompletedCountInRange(start: Long, end: Long): Int
    suspend fun updateTask(task: Task)
    suspend fun updateTaskStatus(taskId: String, status: TaskStatus, now: Long)
    suspend fun softDeleteTask(taskId: String, now: Long)
    suspend fun markAsCompleted(taskId: String, now: Long)
}
