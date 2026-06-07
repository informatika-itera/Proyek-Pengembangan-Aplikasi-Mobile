package com.studyhub.domain.usecase.task

import com.studyhub.domain.model.Task
import com.studyhub.domain.repository.TaskRepository
import com.studyhub.domain.usecase.notification.ScheduleSmartReminderUseCase

class AddTaskUseCase(
    private val taskRepository: TaskRepository,
    private val scheduleSmartReminderUseCase: ScheduleSmartReminderUseCase
) {
    suspend operator fun invoke(task: Task) {
        require(task.title.isNotBlank()) {
            "Judul tidak boleh kosong"
        }
        require(task.dueDate > 0) { "Deadline harus valid" }

        taskRepository.addTask(task)

        // Schedule reminder async — non-blocking
        try {
            scheduleSmartReminderUseCase(task.id)
        } catch (e: Exception) {
            // Non-critical — task already saved
        }
    }
}
