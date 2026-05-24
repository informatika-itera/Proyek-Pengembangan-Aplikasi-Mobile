package com.studyhub.domain.usecase.task

import com.studyhub.domain.model.TaskStatus
import com.studyhub.domain.repository.TaskRepository

class UpdateTaskStatusUseCase(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(taskId: String, status: TaskStatus) {
        require(taskId.isNotBlank()) { "TaskId tidak boleh kosong" }
        taskRepository.updateTaskStatus(taskId, status, kotlinx.datetime.Clock.System.now().toEpochMilliseconds())
    }
}
