package com.studyhub.domain.usecase.notification

import com.studyhub.domain.repository.PreferencesRepository
import com.studyhub.domain.repository.ReminderRepository
import com.studyhub.domain.repository.TaskRepository
import com.studyhub.domain.usecase.ai.GetSmartReminderUseCase
import kotlinx.coroutines.flow.first

class ScheduleSmartReminderUseCase(
    private val getSmartReminderUseCase: GetSmartReminderUseCase,
    private val reminderRepository: ReminderRepository,
    private val preferencesRepository: PreferencesRepository,
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(taskId: String) {
        val prefs = preferencesRepository.userPreferences.first()
        if (!prefs.notificationEnabled || !prefs.isAiReminderEnabled) return

        try {
            val schedule = getSmartReminderUseCase(taskId)
            val task = taskRepository.getTaskById(taskId).first()
                ?: return

            reminderRepository.scheduleReminder(
                taskId = taskId,
                scheduledAt = schedule.suggestedReminderTime,
                aiReason = schedule.adaptiveReason,
                taskTitle = task.title,
                taskSubject = task.subject
            )
        } catch (e: Exception) {
            // Fail silently — reminder is non-critical
        }
    }
}
