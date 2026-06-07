package com.studyhub.core.notification

class IosNotificationManager : NotificationManager {
    override fun showSimpleNotification(id: Int, title: String, body: String, taskId: String?) {
        // iOS implementation using UNUserNotificationCenter
    }

    override fun showPomodoroNotification(
        id: Int, title: String, body: String, phase: String, timeRemaining: Int, isOngoing: Boolean
    ) {
        // iOS implementation
    }

    override fun cancelNotification(id: Int) {
        // iOS implementation
    }
}
