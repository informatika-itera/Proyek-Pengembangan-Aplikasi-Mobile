package com.example.tabungin.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_SHOW_REMINDER -> {
                val namaUser = intent.getStringExtra(AlarmScheduler.EXTRA_USER_NAME) ?: ""
                NotificationHelper.showReminderNotification(context, namaUser)

                // Reschedule for next day
                val alarmScheduler = AlarmScheduler(context)
                val prefs = context.getSharedPreferences("tabungin_notif_prefs", Context.MODE_PRIVATE)
                val hour = prefs.getInt("notifikasi_jam", 9)
                val minute = prefs.getInt("notifikasi_menit", 0)
                alarmScheduler.scheduleDailyReminder(hour, minute, namaUser)
            }
        }
    }

    companion object {
        const val ACTION_SHOW_REMINDER = "com.example.tabungin.SHOW_REMINDER"
    }
}