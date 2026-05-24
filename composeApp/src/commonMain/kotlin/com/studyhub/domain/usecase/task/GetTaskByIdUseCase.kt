package com.studyhub.domain.usecase.task

import com.studyhub.domain.model.Task
import com.studyhub.domain.repository.TaskRepository

class GetTaskByIdUseCase(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(taskId: String): Task? {
        require(taskId.isNotBlank()) { "TaskId tidak boleh kosong" }
        return taskRepository.getTaskById(taskId)
    }
}
