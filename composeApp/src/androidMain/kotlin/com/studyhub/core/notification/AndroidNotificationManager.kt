package com.studyhub.core.notification

import android.app.NotificationManager as AndroidSystemNotifManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.studyhub.MainActivity
import com.studyhub.R
import com.studyhub.notification.NotificationChannels

class AndroidNotificationManager(
    private val context: Context
) : NotificationManager {

    private val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as AndroidSystemNotifManager

    override fun showSimpleNotification(id: Int, title: String, body: String, taskId: String?) {
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("taskId", taskId)
        }
        val openPendingIntent = PendingIntent.getActivity(
            context, id, openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notif = NotificationCompat.Builder(context, NotificationChannels.CHANNEL_REMINDER)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(openPendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        manager.notify(id, notif)
    }

    override fun showPomodoroNotification(
        id: Int, title: String, body: String, phase: String, timeRemaining: Int, isOngoing: Boolean
    ) {
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("openScreen", "pomodoro")
        }
        val openPendingIntent = PendingIntent.getActivity(
            context, id, openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notifBuilder = NotificationCompat.Builder(context, NotificationChannels.CHANNEL_POMODORO)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(openPendingIntent)
            .setOngoing(isOngoing)
            .setOnlyAlertOnce(isOngoing)
            .setSilent(isOngoing)
            .setPriority(if (isOngoing) NotificationCompat.PRIORITY_LOW else NotificationCompat.PRIORITY_HIGH)

        if (!isOngoing) {
            notifBuilder.setDefaults(NotificationCompat.DEFAULT_ALL)
            notifBuilder.setFullScreenIntent(openPendingIntent, true)
        }

        manager.notify(id, notifBuilder.build())
    }

    override fun cancelNotification(id: Int) {
        manager.cancel(id)
    }
}
