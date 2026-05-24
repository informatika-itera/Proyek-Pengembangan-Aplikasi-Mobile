package com.studyhub.domain.usecase.task

import com.studyhub.domain.repository.TaskRepository

class DeleteTaskUseCase(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(taskId: String) {
        require(taskId.isNotBlank()) { "TaskId tidak boleh kosong" }
        taskRepository.softDeleteTask(taskId, kotlinx.datetime.Clock.System.now().toEpochMilliseconds())
    }
}
