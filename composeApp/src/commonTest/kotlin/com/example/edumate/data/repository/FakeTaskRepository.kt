package com.example.edumate.data.repository

import com.example.edumate.domain.model.Task
import com.example.edumate.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeTaskRepository : TaskRepository {
    private val tasksFlow = MutableStateFlow<List<Task>>(emptyList())
    private var nextId = 1L

    override fun getAllTasks(): Flow<List<Task>> {
        return tasksFlow
    }

    override fun getTaskById(id: Long): Flow<Task?> {
        return tasksFlow.map { tasks -> tasks.find { it.id == id } }
    }

    override suspend fun insertTask(task: Task): Long {
        val newTask = task.copy(id = nextId++)
        tasksFlow.update { it + newTask }
        return newTask.id
    }

    override suspend fun updateTask(task: Task) {
        tasksFlow.update { currentTasks ->
            currentTasks.map { if (it.id == task.id) task else it }
        }
    }

    override suspend fun deleteTask(id: Long) {
        tasksFlow.update { currentTasks ->
            currentTasks.filterNot { it.id == id }
        }
    }

    override suspend fun toggleComplete(id: Long) {
        tasksFlow.update { currentTasks ->
            currentTasks.map {
                if (it.id == id) it.copy(isCompleted = !it.isCompleted) else it
            }
        }
    }

    fun emitTasks(tasks: List<Task>) {
        tasksFlow.value = tasks
    }
}