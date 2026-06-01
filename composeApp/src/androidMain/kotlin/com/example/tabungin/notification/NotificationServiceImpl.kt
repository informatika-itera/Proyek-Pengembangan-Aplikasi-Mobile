package com.example.tabungin.notification

import android.content.Context

class NotificationServiceImpl(
    private val context: Context,
    private val alarmScheduler: AlarmScheduler
) : NotificationService {

    override fun showTargetAchievedNotification(targetName: String, amount: String) {
        NotificationHelper.showTargetAchievedNotification(context, targetName, amount)
    }

    override fun scheduleDailyReminder(hour: Int, minute: Int) {
        alarmScheduler.scheduleDailyReminder(hour, minute)
    }

    override fun cancelDailyReminder() {
        alarmScheduler.cancelDailyReminder()
    }
}
