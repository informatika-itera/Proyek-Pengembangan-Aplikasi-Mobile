package com.example.tabungin.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.tabungin.MainActivity
import com.example.tabungin.R

object NotificationHelper {

    const val CHANNEL_ID_REMINDER = "tabungin_reminder_channel"
    const val CHANNEL_ID_TARGET = "tabungin_target_channel"

    const val CHANNEL_NAME_REMINDER = "Pengingat Menabung"
    const val CHANNEL_NAME_TARGET = "Target Tercapai"

    const val NOTIFICATION_ID_REMINDER = 1001
    const val NOTIFICATION_ID_TARGET = 1002

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Reminder channel
            val reminderChannel = NotificationChannel(
                CHANNEL_ID_REMINDER,
                CHANNEL_NAME_REMINDER,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifikasi pengingat harian untuk menabung"
                enableVibration(true)
            }

            // Target achieved channel
            val targetChannel = NotificationChannel(
                CHANNEL_ID_TARGET,
                CHANNEL_NAME_TARGET,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi saat target tabungan tercapai"
                enableVibration(true)
                enableLights(true)
            }

            notificationManager.createNotificationChannels(listOf(reminderChannel, targetChannel))
        }
    }

    fun showTargetAchievedNotification(context: Context, targetName: String, amount: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_TARGET)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🎉 Target Tercapai!")
            .setContentText("$targetName sudah tercapai! Rp $amount")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_TARGET, notification)
        } catch (e: SecurityException) {
            // Handle case where notification permission is not granted
        }
    }

    fun showReminderNotification(context: Context, namaUser: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val greeting = if (namaUser.isNotBlank()) ", $namaUser" else ""

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_REMINDER)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("💰 Waktunya Menabung!")
            .setContentText("Hai$greeting, jangan lupa menabung hari ini ya!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_REMINDER, notification)
        } catch (e: SecurityException) {
            // Handle case where notification permission is not granted
        }
    }

    fun cancelAllNotifications(context: Context) {
        NotificationManagerCompat.from(context).cancelAll()
    }
}