package com.studyhub.domain.repository

import com.studyhub.domain.model.ReminderInfo

interface ReminderRepository {
    suspend fun scheduleReminder(
        taskId: String,
        scheduledAt: Long,
        aiReason: String,
        taskTitle: String,
        taskSubject: String
    )

    suspend fun cancelReminder(taskId: String)
    suspend fun cancelAllReminders()
    suspend fun getReminderForTask(taskId: String): ReminderInfo?
    suspend fun getAllActiveReminders(): List<ReminderInfo>
    suspend fun rescheduleAllOnBoot()
}
