package com.studyhub.data.repository

import com.studyhub.data.local.StudyHubDatabase
import com.studyhub.domain.model.Task
import com.studyhub.domain.repository.TaskRepository
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.studyhub.data.local.TaskEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class TaskRepositoryImpl(
    private val db: StudyHubDatabase
) : TaskRepository {

    private val queries = db.taskQueries

    override fun getAllTasks(): Flow<List<Task>> {
        return queries.getAllTasks()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toTask() } }
    }

    override fun getTaskById(id: Long): Flow<Task?> {
        return queries.getTaskById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toTask() }
    }

    override suspend fun insertTask(task: Task) {
        val now = Clock.System.now().toEpochMilliseconds()
        queries.insertTask(
            title = task.title,
            description = task.description,
            category = task.category,
            priority = task.priority,
            progress = task.progress.toLong(),
            dueDate = task.dueDate,
            startTime = task.startTime,
            endTime = task.endTime,
            colorHex = task.colorHex,
            created_at = now,
            updated_at = now
        )
    }

    override suspend fun updateTask(task: Task) {
        val now = Clock.System.now().toEpochMilliseconds()
        queries.updateTask(
            title = task.title,
            description = task.description,
            category = task.category,
            priority = task.priority,
            progress = task.progress.toLong(),
            dueDate = task.dueDate,
            startTime = task.startTime,
            endTime = task.endTime,
            colorHex = task.colorHex,
            updated_at = now,
            id = task.id ?: 0L
        )
    }

    override suspend fun deleteTask(id: Long) {
        queries.deleteTaskById(id)
    }

    private fun TaskEntity.toTask(): Task {
        return Task(
            id = id,
            title = title,
            description = description,
            category = category,
            priority = priority,
            progress = progress.toInt(),
            dueDate = dueDate,
            startTime = startTime,
            endTime = endTime,
            colorHex = colorHex
        )
    }
}
