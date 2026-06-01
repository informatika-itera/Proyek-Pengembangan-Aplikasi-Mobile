package com.example.tabungin.notification

interface NotificationService {
    fun showTargetAchievedNotification(targetName: String, amount: String)
    fun scheduleDailyReminder(hour: Int, minute: Int)
    fun cancelDailyReminder()
}
