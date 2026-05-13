package com.studyhub.domain.usecase

import com.studyhub.domain.model.ReminderSchedule
import com.studyhub.domain.repository.AiRepository
import com.studyhub.domain.repository.TaskRepository

class GetSmartReminderUseCase(
    private val aiRepository: AiRepository,
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(taskId: String, userId: String): ReminderSchedule {
        val task = taskRepository.getTaskById(taskId)
        val history = taskRepository.getCompletedTasks(userId)
        return aiRepository.getSmartReminder(task, history)
    }
}
