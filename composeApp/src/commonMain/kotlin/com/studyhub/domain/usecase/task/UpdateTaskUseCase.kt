package com.studyhub.domain.usecase.task

import com.studyhub.domain.model.Task
import com.studyhub.domain.repository.TaskRepository

class UpdateTaskUseCase(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(task: Task) {
        require(task.id.isNotBlank()) { "TaskId tidak boleh kosong" }
        require(task.title.isNotBlank()) { "Judul tidak boleh kosong" }
        taskRepository.updateTask(task)
    }
}
