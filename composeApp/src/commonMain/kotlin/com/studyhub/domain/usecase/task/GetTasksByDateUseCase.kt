package com.studyhub.domain.usecase.task

import com.studyhub.domain.model.Task
import com.studyhub.domain.repository.TaskRepository
import kotlinx.datetime.LocalDate

class GetTasksByDateUseCase(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(date: LocalDate): List<Task> {
        return taskRepository.getTasksByDate(date)
    }
}
