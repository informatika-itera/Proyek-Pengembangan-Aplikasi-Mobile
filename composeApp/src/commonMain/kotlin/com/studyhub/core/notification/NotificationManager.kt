package com.studyhub.core.notification

interface NotificationManager {
    fun showSimpleNotification(
        id: Int,
        title: String,
        body: String,
        taskId: String? = null
    )
    
    fun showPomodoroNotification(
        id: Int,
        title: String,
        body: String,
        phase: String,
        timeRemaining: Int,
        isOngoing: Boolean = false
    )
    
    fun cancelNotification(id: Int)
}
