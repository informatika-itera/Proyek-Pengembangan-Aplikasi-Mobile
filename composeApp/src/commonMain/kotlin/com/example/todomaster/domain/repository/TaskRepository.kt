package com.example.todomaster.domain.repository

import com.example.todomaster.domain.model.Quadrant
import com.example.todomaster.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    fun getAllTasks(): Flow<List<Task>>
    fun getTasksByPriority(priority: Quadrant): Flow<List<Task>>
    suspend fun getTaskById(id: Long): Task?
    suspend fun insertTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(id: Long)
    suspend fun toggleTaskCompletion(id: Long, isCompleted: Boolean)
    suspend fun getTodayDoFirstCount(todayStartMillis: Long): Long
}