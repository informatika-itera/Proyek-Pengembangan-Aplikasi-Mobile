package com.example.edumate.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.edumate.data.local.TaskDatabase
import com.example.edumate.data.local.entity.toDomain
import com.example.edumate.data.local.entity.toDomainList
import com.example.edumate.domain.model.Task
import com.example.edumate.domain.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class TaskRepositoryImpl(private val database: TaskDatabase) : TaskRepository {

    private val queries = database.taskQueries

    override fun getAllTasks(): Flow<List<Task>> {
        return queries.getAllTasks()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { it.toDomainList() }
    }

    override fun getTaskById(id: Long): Flow<Task?> {
        return queries.getTaskById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { it?.toDomain() }
    }

    override suspend fun insertTask(task: Task): Long = withContext(Dispatchers.Default) {
        queries.insertTask(
            title = task.title,
            description = task.description,
            priority = task.priority.name,
            deadline = task.deadline?.toEpochMilliseconds(),
            is_completed = if (task.isCompleted) 1L else 0L,
            sub_tasks = task.subTasks,
            created_at = task.createdAt.toEpochMilliseconds(),
            updated_at = task.updatedAt.toEpochMilliseconds()
        )
        queries.lastInsertId().executeAsOne()
    }

    override suspend fun updateTask(task: Task) {
        withContext(Dispatchers.Default) {
            queries.updateTask(
                title = task.title,
                description = task.description,
                priority = task.priority.name,
                deadline = task.deadline?.toEpochMilliseconds(),
                is_completed = if (task.isCompleted) 1L else 0L,
                sub_tasks = task.subTasks,
                updated_at = Clock.System.now().toEpochMilliseconds(),
                id = task.id
            )
        }
    }

    override suspend fun deleteTask(id: Long) {
        withContext(Dispatchers.Default) {
            queries.deleteTaskById(id)
        }
    }

    override suspend fun toggleComplete(id: Long) {
        withContext(Dispatchers.Default) {
            queries.toggleComplete(
                updated_at = Clock.System.now().toEpochMilliseconds(),
                id = id
            )
        }
    }
}