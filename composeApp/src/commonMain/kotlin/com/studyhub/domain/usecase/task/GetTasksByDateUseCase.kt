package com.studyhub.domain.usecase.task

import com.studyhub.domain.model.Task
import com.studyhub.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

class GetTasksByDateUseCase(private val taskRepository: TaskRepository) {
    operator fun invoke(date: LocalDate): Flow<List<Task>> {
        return taskRepository.getTasksByDate(date)
    }
}
