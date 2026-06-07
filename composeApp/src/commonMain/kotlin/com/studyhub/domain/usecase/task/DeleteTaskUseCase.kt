package com.studyhub.domain.usecase.task

import com.studyhub.domain.repository.TaskRepository
import com.studyhub.domain.usecase.notification.CancelReminderUseCase
import kotlinx.datetime.Clock

class DeleteTaskUseCase(
    private val taskRepository: TaskRepository,
    private val cancelReminderUseCase: CancelReminderUseCase
) {
    suspend operator fun invoke(taskId: String) {
        require(taskId.isNotBlank()) {
            "TaskId tidak boleh kosong"
        }
        taskRepository.softDeleteTask(taskId, Clock.System.now().toEpochMilliseconds())

        // Cancel reminder when task deleted
        try {
            cancelReminderUseCase(taskId)
        } catch (e: Exception) { }
    }
}
