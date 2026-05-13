package com.studyhub.domain.repository

import com.studyhub.domain.model.PriorityResult
import com.studyhub.domain.model.ReminderSchedule
import com.studyhub.domain.model.Task

interface AiRepository {
    suspend fun getSmartPriority(tasks: List<Task>): List<PriorityResult>
    suspend fun getSmartReminder(task: Task, userHistory: List<Task>): ReminderSchedule
}
