package com.studyhub.domain.fake

import com.studyhub.domain.model.Task
import com.studyhub.domain.model.TaskStatus
import com.studyhub.domain.repository.TaskRepository
import kotlinx.datetime.LocalDate

class FakeTaskRepository : TaskRepository {
    val tasks = mutableListOf<Task>()

    override suspend fun addTask(task: Task) { tasks.add(task) }
    override suspend fun getTaskById(taskId: String): Task? = tasks.find { it.id == taskId }
    override suspend fun getAllTasks(): List<Task> = tasks
    override suspend fun getActiveTasks(): List<Task> = tasks.filter { it.status != TaskStatus.DONE }
    override suspend fun getCompletedTasks(): List<Task> = tasks.filter { it.status == TaskStatus.DONE }
    override suspend fun getTasksByDate(date: LocalDate): List<Task> = tasks
    override suspend fun getTasksBySubject(subject: String): List<Task> = tasks.filter { it.subject == subject }
    override suspend fun getOverdueCount(now: Long): Int = tasks.count { it.dueDate < now && it.status != TaskStatus.DONE }
    override suspend fun getCompletedCountInRange(start: Long, end: Long): Int = 0
    override suspend fun updateTask(task: Task) {
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) tasks[index] = task
    }
    override suspend fun updateTaskStatus(taskId: String, status: TaskStatus, now: Long) {
        val index = tasks.indexOfFirst { it.id == taskId }
        if (index != -1) tasks[index] = tasks[index].copy(status = status)
    }
    override suspend fun softDeleteTask(taskId: String, now: Long) {
        tasks.removeIf { it.id == taskId }
    }
    override suspend fun markAsCompleted(taskId: String, now: Long) {
        updateTaskStatus(taskId, TaskStatus.DONE, now)
    }
}
