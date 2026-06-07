package com.example.todomaster.data.repository

import com.example.todomaster.domain.model.Quadrant
import com.example.todomaster.domain.model.Task
import com.example.todomaster.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeTaskRepository : TaskRepository {
    private val tasksFlow = MutableStateFlow<List<Task>>(emptyList())

    override fun getAllTasks(): Flow<List<Task>> = tasksFlow

    override fun getTasksByPriority(priority: Quadrant): Flow<List<Task>> {
        return tasksFlow.map { tasks ->
            tasks.filter { it.priority == priority }
        }
    }

    override suspend fun insertTask(task: Task) {
        val currentList = tasksFlow.value.toMutableList()
        val newId = (currentList.maxOfOrNull { it.id } ?: 0L) + 1
        currentList.add(task.copy(id = newId))
        tasksFlow.value = currentList
    }

    override suspend fun updateTask(task: Task) {
        val currentList = tasksFlow.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == task.id }
        if (index != -1) {
            currentList[index] = task
            tasksFlow.value = currentList
        }
    }

    override suspend fun deleteTask(id: Long) {
        val currentList = tasksFlow.value.toMutableList()
        currentList.removeAll { it.id == id }
        tasksFlow.value = currentList
    }

    override suspend fun getTaskById(id: Long): Task? {
        return tasksFlow.value.find { it.id == id }
    }

    override suspend fun toggleTaskCompletion(id: Long, isCompleted: Boolean) {
        val currentList = tasksFlow.value.map {
            if (it.id == id) it.copy(isCompleted = isCompleted) else it
        }
        tasksFlow.value = currentList
    }

    override suspend fun getTodayDoFirstCount(todayStartMillis: Long): Long {
        return tasksFlow.value.count {
            it.priority == Quadrant.DO_FIRST && it.createdAt >= todayStartMillis
        }.toLong()
    }
}