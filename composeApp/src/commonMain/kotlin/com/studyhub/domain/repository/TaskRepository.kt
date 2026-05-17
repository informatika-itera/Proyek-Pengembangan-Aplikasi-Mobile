package com.studyhub.domain.repository

import com.studyhub.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    fun getTaskById(id: Long): Flow<Task?>
    suspend fun insertTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(id: Long)
}
