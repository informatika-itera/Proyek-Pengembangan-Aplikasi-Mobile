package com.studyhub.domain.usecase.task

import com.studyhub.domain.model.Task
import com.studyhub.domain.repository.TaskRepository

class AddTaskUseCase(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(task: Task) {
        require(task.title.isNotBlank()) { "Judul tidak boleh kosong" }
        require(task.dueDate > 0) { "Deadline harus valid" }
        taskRepository.addTask(task)
    }
}
