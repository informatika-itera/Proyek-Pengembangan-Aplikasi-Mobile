package com.example.todomaster.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.todomaster.data.local.TaskDatabase
import com.example.todomaster.data.local.entity.toDomainModel
import com.example.todomaster.domain.model.Quadrant
import com.example.todomaster.domain.model.Task
import com.example.todomaster.domain.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(
    db: TaskDatabase
) : TaskRepository {

    private val queries = db.taskQueries

    override fun getAllTasks(): Flow<List<Task>> {
        return queries.selectAllTasks()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomainModel() } }
    }

    override fun getTasksByPriority(priority: Quadrant): Flow<List<Task>> {
        return queries.selectTasksByPriority(priority.value)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomainModel() } }
    }

    override suspend fun getTaskById(id: Long): Task? {
        return queries.selectTaskById(id).executeAsOneOrNull()?.toDomainModel()
    }

    override suspend fun insertTask(task: Task) {
        queries.insertTask(
            title = task.title,
            description = task.description,
            priority_type = task.priority.value,
            due_date = task.dueDate,
            is_completed = if (task.isCompleted) 1L else 0L,
            is_pinned = if (task.isPinned) 1L else 0L,
            sub_tasks = task.subTasks.joinToString(","),
            created_at = task.createdAt
        )
    }

    override suspend fun updateTask(task: Task) {
        queries.updateTask(
            title = task.title,
            description = task.description,
            priority_type = task.priority.value,
            due_date = task.dueDate,
            is_completed = if (task.isCompleted) 1L else 0L,
            is_pinned = if (task.isPinned) 1L else 0L,
            sub_tasks = task.subTasks.joinToString(","),
            id = task.id
        )
    }

    override suspend fun deleteTask(id: Long) {
        queries.deleteTaskById(id)
    }

    override suspend fun toggleTaskCompletion(id: Long, isCompleted: Boolean) {
        queries.toggleTaskCompletion(if (isCompleted) 1L else 0L, id)
    }

    override suspend fun getTodayDoFirstCount(todayStartMillis: Long): Long {
        return queries.selectTodayDoFirstCount(todayStartMillis).executeAsOne()
    }
}