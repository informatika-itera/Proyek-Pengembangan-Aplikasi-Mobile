package com.studymate.core.util

interface NotificationScheduler {
    fun scheduleReminder(id: Long, title: String, dueDate: Long)
}
