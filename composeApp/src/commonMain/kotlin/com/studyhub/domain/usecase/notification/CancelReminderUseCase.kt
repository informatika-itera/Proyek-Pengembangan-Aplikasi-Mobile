package com.studyhub.domain.usecase.notification

import com.studyhub.domain.repository.ReminderRepository

class CancelReminderUseCase(
    private val reminderRepository: ReminderRepository
) {
    suspend operator fun invoke(taskId: String) =
        reminderRepository.cancelReminder(taskId)

    suspend fun cancelAll() = reminderRepository.cancelAllReminders()
}
