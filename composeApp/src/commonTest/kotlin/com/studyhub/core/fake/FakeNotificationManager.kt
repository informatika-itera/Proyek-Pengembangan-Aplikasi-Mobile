package com.studyhub.core.fake

import com.studyhub.core.notification.NotificationManager

class FakeNotificationManager : NotificationManager {
    var lastNotificationTitle: String? = null
    var cancelCalledCount = 0

    override fun showSimpleNotification(id: Int, title: String, body: String, taskId: String?) {
        lastNotificationTitle = title
    }

    override fun showPomodoroNotification(
        id: Int, title: String, body: String,
        phase: String, timeRemaining: Int, isOngoing: Boolean
    ) {
        lastNotificationTitle = title
    }

    override fun cancelNotification(id: Int) {
        cancelCalledCount++
    }
}
