package com.studyhub.domain.usecase.task

import com.studyhub.domain.model.Task
import com.studyhub.domain.repository.TaskRepository

class GetActiveTasksUseCase(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(): List<Task> {
        return taskRepository.getActiveTasks()
    }
}
