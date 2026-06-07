package com.studyhub.domain.usecase.task

import com.studyhub.domain.model.Task
import com.studyhub.domain.repository.TaskRepository
import com.studyhub.domain.usecase.notification.CancelReminderUseCase
import com.studyhub.domain.usecase.notification.ScheduleSmartReminderUseCase

class UpdateTaskUseCase(
    private val taskRepository: TaskRepository,
    private val cancelReminderUseCase: CancelReminderUseCase,
    private val scheduleSmartReminderUseCase: ScheduleSmartReminderUseCase
) {
    suspend operator fun invoke(task: Task) {
        require(task.id.isNotBlank()) {
            "TaskId tidak boleh kosong"
        }
        require(task.title.isNotBlank()) {
            "Judul tidak boleh kosong"
        }
        taskRepository.updateTask(task)

        // Re-schedule reminder when deadline changes
        try {
            cancelReminderUseCase(task.id)
            scheduleSmartReminderUseCase(task.id)
        } catch (e: Exception) { }
    }
}
