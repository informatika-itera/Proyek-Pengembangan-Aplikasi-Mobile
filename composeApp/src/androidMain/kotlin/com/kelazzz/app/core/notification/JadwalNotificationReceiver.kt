package com.kelazzz.app.core.notification

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.kelazzz.app.MainActivity
import com.kelazzz.app.R

class JadwalNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w(TAG, "Notification skipped because POST_NOTIFICATIONS is not granted")
            return
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        ensureChannel(notificationManager)

        val jadwalId = intent.getLongExtra(EXTRA_JADWAL_ID, 0L)
        val title = intent.getStringExtra(EXTRA_TITLE).orEmpty().ifBlank { "Agenda Akademik" }
        val description = intent.getStringExtra(EXTRA_DESCRIPTION).orEmpty()
        val date = intent.getStringExtra(EXTRA_DATE).orEmpty()
        val time = intent.getStringExtra(EXTRA_TIME).orEmpty()
        val kind = intent.getStringExtra(EXTRA_KIND).orEmpty()
        val offsetMinutes = intent.getLongExtra(EXTRA_OFFSET_MINUTES, 0L)

        val contentTitle = when {
            kind.isBlank() -> title
            else -> "$kind: $title"
        }
        val contentText = buildContentText(offsetMinutes, date, time, description)

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentIntent = PendingIntent.getActivity(
            context,
            jadwalId.toNotificationRequestCode(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, CHANNEL_ID)
        } else {
            Notification.Builder(context)
        }
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setStyle(Notification.BigTextStyle().bigText(contentText))
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .setShowWhen(true)
            .build()

        notificationManager.notify(jadwalId.toNotificationRequestCode(), notification)
    }

    private fun ensureChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val existing = notificationManager.getNotificationChannel(CHANNEL_ID)
        if (existing != null) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Pengingat Jadwal",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifikasi pengingat agenda akademik KelazZz"
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun buildContentText(
        offsetMinutes: Long,
        date: String,
        time: String,
        description: String
    ): String {
        val reminderText = when (offsetMinutes) {
            1L -> "dimulai 1 menit lagi"
            10L -> "dimulai 10 menit lagi"
            30L -> "dimulai 30 menit lagi"
            60L -> "dimulai 1 jam lagi"
            1440L -> "dimulai 1 hari lagi"
            else -> "akan segera dimulai"
        }
        val scheduleText = listOf(date, time).filter { it.isNotBlank() }.joinToString(" ")
        return buildString {
            append("Agenda $reminderText")
            if (scheduleText.isNotBlank()) append(" ($scheduleText)")
            if (description.isNotBlank()) {
                append(". ")
                append(description)
            }
        }
    }

    companion object {
        private const val TAG = "JadwalReminder"
        const val CHANNEL_ID = "jadwal_reminders"
        const val EXTRA_JADWAL_ID = "extra_jadwal_id"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_DESCRIPTION = "extra_description"
        const val EXTRA_DATE = "extra_date"
        const val EXTRA_TIME = "extra_time"
        const val EXTRA_KIND = "extra_kind"
        const val EXTRA_OFFSET_MINUTES = "extra_offset_minutes"
    }
}

private fun Long.toNotificationRequestCode(): Int {
    return (this and 0x7fffffff).toInt()
}
