package com.studyhub.domain.usecase.ai

import com.studyhub.domain.model.ReminderSchedule
import com.studyhub.domain.repository.AiRepository
import com.studyhub.domain.repository.TaskRepository
import kotlinx.coroutines.flow.first

class GetSmartReminderUseCase(
    private val aiRepository: AiRepository,
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(taskId: String): ReminderSchedule {
        require(taskId.isNotBlank()) { "TaskId tidak boleh kosong" }
        val task = taskRepository.getTaskById(taskId).first()
            ?: throw Exception("Task tidak ditemukan")
        val history = taskRepository.getCompletedTasks().first()
        return aiRepository.getSmartReminder(task, history)
    }
}
