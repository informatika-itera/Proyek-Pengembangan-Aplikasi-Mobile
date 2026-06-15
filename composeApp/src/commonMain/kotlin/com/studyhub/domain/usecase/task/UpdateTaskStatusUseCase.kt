package com.studyhub.domain.usecase.task

import com.studyhub.domain.model.TaskStatus
import com.studyhub.domain.repository.TaskRepository
import kotlinx.datetime.Clock

class UpdateTaskStatusUseCase(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(taskId: String, status: TaskStatus) {
        require(taskId.isNotBlank()) { "TaskId tidak boleh kosong" }
        val now = Clock.System.now().toEpochMilliseconds()
        taskRepository.updateTaskStatus(taskId, status, now)
    }
}
