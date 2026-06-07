package com.studyhub.domain.fake

import com.studyhub.domain.model.Task
import com.studyhub.domain.model.TaskStatus
import com.studyhub.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate

class FakeTaskRepository : TaskRepository {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks = mutableListOf<Task>()

    var shouldThrow = false

    private fun updateFlow() {
        _tasks.value = tasks.toList()
    }

    fun addTasks(list: List<Task>) {
        tasks.addAll(list)
        updateFlow()
    }

    override suspend fun addTask(task: Task) {
        if (shouldThrow) throw Exception("Fake error")
        tasks.add(task)
        updateFlow()
    }

    override fun getTaskById(taskId: String): Flow<Task?> {
        if (shouldThrow) throw Exception("Fake error")
        return _tasks.map { it.find { t -> t.id == taskId } }
    }

    override fun getAllTasks(): Flow<List<Task>> {
        if (shouldThrow) throw Exception("Fake error")
        return _tasks
    }

    override fun getActiveTasks(): Flow<List<Task>> {
        if (shouldThrow) throw Exception("Fake error")
        return _tasks.map { it.filter { t -> !t.isDeleted && t.status != TaskStatus.DONE } }
    }

    override fun getCompletedTasks(): Flow<List<Task>> {
        if (shouldThrow) throw Exception("Fake error")
        return _tasks.map { it.filter { t -> t.status == TaskStatus.DONE } }
    }

    override fun getTasksByDate(date: LocalDate): Flow<List<Task>> {
        if (shouldThrow) throw Exception("Fake error")
        return _tasks
    }

    override fun getTasksBySubject(subject: String): Flow<List<Task>> {
        if (shouldThrow) throw Exception("Fake error")
        return _tasks.map { it.filter { t -> t.subject == subject } }
    }

    override suspend fun getOverdueCount(now: Long): Int {
        return tasks.count { it.dueDate < now && it.status != TaskStatus.DONE }
    }

    override suspend fun getCompletedCountInRange(start: Long, end: Long): Int = 0

    override suspend fun updateTask(task: Task) {
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasks[index] = task
            updateFlow()
        }
    }

    override suspend fun updateTaskStatus(taskId: String, status: TaskStatus, now: Long) {
        val index = tasks.indexOfFirst { it.id == taskId }
        if (index != -1) {
            tasks[index] = tasks[index].copy(status = status, updatedAt = now)
            updateFlow()
        }
    }

    override suspend fun softDeleteTask(taskId: String, now: Long) {
        val index = tasks.indexOfFirst { it.id == taskId }
        if (index != -1) {
            tasks[index] = tasks[index].copy(isDeleted = true, updatedAt = now)
            updateFlow()
        }
    }

    override suspend fun markAsCompleted(taskId: String, now: Long) {
        updateTaskStatus(taskId, TaskStatus.DONE, now)
    }
}
