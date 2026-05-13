package com.studyhub.domain.repository

import com.studyhub.domain.model.Task

interface TaskRepository {
    suspend fun getActiveTasks(userId: String): List<Task>
    suspend fun getCompletedTasks(userId: String): List<Task>
    suspend fun getTaskById(taskId: String): Task
}
