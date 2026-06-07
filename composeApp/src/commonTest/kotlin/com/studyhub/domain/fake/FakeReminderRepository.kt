package com.studyhub.domain.fake

import com.studyhub.domain.model.ReminderInfo
import com.studyhub.domain.repository.ReminderRepository

class FakeReminderRepository : ReminderRepository {
    val reminders = mutableListOf<ReminderInfo>()

    override suspend fun scheduleReminder(
        taskId: String,
        scheduledAt: Long,
        aiReason: String,
        taskTitle: String,
        taskSubject: String
    ) {
        reminders.add(ReminderInfo(taskId, scheduledAt, aiReason, true, 0L))
    }

    override suspend fun cancelReminder(taskId: String) {
        reminders.removeAll { it.taskId == taskId }
    }

    override suspend fun cancelAllReminders() {
        reminders.clear()
    }

    override suspend fun getReminderForTask(taskId: String): ReminderInfo? {
        return reminders.find { it.taskId == taskId }
    }

    override suspend fun getAllActiveReminders(): List<ReminderInfo> = reminders

    override suspend fun rescheduleAllOnBoot() {}
}
