package com.studyhub.domain.usecase.task

import com.studyhub.domain.model.Task
import com.studyhub.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetTaskByIdUseCase(private val taskRepository: TaskRepository) {
    operator fun invoke(taskId: String): Flow<Task?> {
        require(taskId.isNotBlank()) { "TaskId tidak boleh kosong" }
        return taskRepository.getTaskById(taskId)
    }
}
