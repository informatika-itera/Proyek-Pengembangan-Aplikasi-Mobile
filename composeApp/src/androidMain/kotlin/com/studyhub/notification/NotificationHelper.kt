package com.studyhub.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.studyhub.MainActivity
import com.studyhub.R

object NotificationChannels {
    const val CHANNEL_REMINDER = "studyhub_reminder"
    const val CHANNEL_REMINDER_NAME = "Smart Reminder"
    const val CHANNEL_POMODORO = "studyhub_pomodoro"
    const val CHANNEL_POMODORO_NAME = "Pomodoro Timer"
}

object NotificationIds {
    const val BASE_REMINDER = 1000
    const val POMODORO_FOCUS_DONE = 2001
    const val POMODORO_BREAK_DONE = 2002
    const val POMODORO_ONGOING = 2003
}

fun createNotificationChannels(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val reminderChannel = NotificationChannel(
            NotificationChannels.CHANNEL_REMINDER,
            NotificationChannels.CHANNEL_REMINDER_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Pengingat tugas dari AI StudyHub"
            enableLights(true)
            lightColor = Color.parseColor("#8B7355")
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 250, 250, 250)
            setShowBadge(true)
        }

        val pomodoroChannel = NotificationChannel(
            NotificationChannels.CHANNEL_POMODORO,
            NotificationChannels.CHANNEL_POMODORO_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifikasi sesi Pomodoro"
            enableLights(true)
            lightColor = Color.parseColor("#D85A30")
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 400, 200, 400, 200, 400)
            setShowBadge(false)
            
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            setSound(soundUri, AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            )
        }

        manager.createNotificationChannel(reminderChannel)
        manager.createNotificationChannel(pomodoroChannel)
    }
}

fun buildReminderNotification(
    context: Context,
    taskId: String,
    taskTitle: String,
    taskSubject: String,
    aiReason: String
): Notification {
    val openIntent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        putExtra("taskId", taskId)
        putExtra("openScreen", "task_detail")
    }
    val openPendingIntent = PendingIntent.getActivity(
        context, taskId.hashCode(), openIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val doneIntent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
        action = "ACTION_MARK_DONE"
        putExtra("taskId", taskId)
    }
    val donePendingIntent = PendingIntent.getBroadcast(
        context, taskId.hashCode() + 1, doneIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    return NotificationCompat.Builder(context, NotificationChannels.CHANNEL_REMINDER)
        .setSmallIcon(R.drawable.ic_notification)
        .setContentTitle("📚 $taskTitle")
        .setContentText("$taskSubject · $aiReason")
        .setStyle(NotificationCompat.BigTextStyle()
            .bigText("Mata kuliah: $taskSubject\nAI: $aiReason")
        )
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_REMINDER)
        .setContentIntent(openPendingIntent)
        .setAutoCancel(true)
        .addAction(R.drawable.ic_check, "Tandai Selesai", donePendingIntent)
        .setDefaults(NotificationCompat.DEFAULT_ALL)
        .build()
}

fun buildFocusDoneNotification(
    context: Context,
    sessionNumber: Int,
    totalSessions: Int,
    linkedTaskTitle: String?
): Notification {
    val openIntent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        putExtra("openScreen", "pomodoro")
    }
    val openPendingIntent = PendingIntent.getActivity(
        context, 2001, openIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val startBreakIntent = Intent(context, PomodoroBroadcastReceiver::class.java).apply {
        action = "ACTION_START_BREAK"
    }
    val startBreakPendingIntent = PendingIntent.getBroadcast(
        context, 2011, startBreakIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val isLastSession = sessionNumber >= totalSessions
    val title = if (isLastSession) "🎉 Semua sesi selesai!" else "✅ Sesi fokus $sessionNumber/$totalSessions selesai!"

    val bodyText = buildString {
        if (linkedTaskTitle != null) append("\"$linkedTaskTitle\" · ")
        if (isLastSession) append("Waktunya istirahat panjang!") else append("Ambil istirahat sebentar")
    }

    return NotificationCompat.Builder(context, NotificationChannels.CHANNEL_POMODORO)
        .setSmallIcon(R.drawable.ic_notification) // Fallback to ic_notification if ic_timer missing
        .setContentTitle(title)
        .setContentText(bodyText)
        .setStyle(NotificationCompat.BigTextStyle().bigText(bodyText))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setDefaults(NotificationCompat.DEFAULT_ALL)
        .setCategory(NotificationCompat.CATEGORY_ALARM)
        .setContentIntent(openPendingIntent)
        .setAutoCancel(true)
        .addAction(
            R.drawable.ic_check, // Using ic_check for now
            if (isLastSession) "Mulai Lagi" else "Mulai Istirahat",
            startBreakPendingIntent
        )
        .setFullScreenIntent(openPendingIntent, true)
        .build()
}

fun buildBreakDoneNotification(
    context: Context,
    isLongBreak: Boolean,
    linkedTaskTitle: String?
): Notification {
    val openIntent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        putExtra("openScreen", "pomodoro")
    }
    val openPendingIntent = PendingIntent.getActivity(
        context, 2002, openIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val startFocusIntent = Intent(context, PomodoroBroadcastReceiver::class.java).apply {
        action = "ACTION_START_FOCUS"
    }
    val startFocusPendingIntent = PendingIntent.getBroadcast(
        context, 2012, startFocusIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val breakType = if (isLongBreak) "Istirahat panjang" else "Istirahat pendek"
    val title = "⏰ $breakType selesai!"
    val bodyText = buildString {
        if (linkedTaskTitle != null) append("Lanjut \"$linkedTaskTitle\"?") else append("Siap untuk sesi fokus berikutnya?")
    }

    return NotificationCompat.Builder(context, NotificationChannels.CHANNEL_POMODORO)
        .setSmallIcon(R.drawable.ic_notification)
        .setContentTitle(title)
        .setContentText(bodyText)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_ALARM)
        .setContentIntent(openPendingIntent)
        .setAutoCancel(true)
        .addAction(R.drawable.ic_check, "Mulai Fokus", startFocusPendingIntent)
        .setDefaults(NotificationCompat.DEFAULT_ALL)
        .setFullScreenIntent(openPendingIntent, true)
        .build()
}

fun buildPomodoroOngoingNotification(
    context: Context,
    phase: String,
    timeRemainingSeconds: Int,
    sessionNumber: Int,
    totalSessions: Int,
    linkedTaskTitle: String?
): Notification {
    val openIntent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        putExtra("openScreen", "pomodoro")
    }
    val openPendingIntent = PendingIntent.getActivity(
        context, 2003, openIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val pauseIntent = Intent(context, PomodoroBroadcastReceiver::class.java).apply { action = "ACTION_PAUSE_POMODORO" }
    val pausePendingIntent = PendingIntent.getBroadcast(
        context, 2013, pauseIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val stopIntent = Intent(context, PomodoroBroadcastReceiver::class.java).apply { action = "ACTION_STOP_POMODORO" }
    val stopPendingIntent = PendingIntent.getBroadcast(
        context, 2014, stopIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val minutes = timeRemainingSeconds / 60
    val seconds = timeRemainingSeconds % 60
    val timeStr = "%02d:%02d".format(minutes, seconds)

    val phaseEmoji = when (phase) {
        "FOCUS" -> "🎯"
        "SHORT_BREAK" -> "☕"
        "LONG_BREAK" -> "🛋️"
        else -> "⏱️"
    }

    val phaseLabel = when (phase) {
        "FOCUS" -> "Sesi fokus $sessionNumber/$totalSessions"
        "SHORT_BREAK" -> "Istirahat pendek"
        "LONG_BREAK" -> "Istirahat panjang"
        else -> phase
    }

    val contentText = buildString {
        append("$phaseEmoji $phaseLabel · $timeStr tersisa")
        if (linkedTaskTitle != null) append(" · $linkedTaskTitle")
    }

    return NotificationCompat.Builder(context, NotificationChannels.CHANNEL_POMODORO)
        .setSmallIcon(R.drawable.ic_notification)
        .setContentTitle("StudyHub Pomodoro")
        .setContentText(contentText)
        .setOngoing(true)
        .setOnlyAlertOnce(true)
        .setSilent(true)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setContentIntent(openPendingIntent)
        .addAction(R.drawable.ic_check, "Jeda", pausePendingIntent) // reusing ic_check for ic_pause
        .addAction(R.drawable.ic_notification, "Stop", stopPendingIntent) // reusing ic_notification for ic_stop
        .setProgress(timeRemainingSeconds, timeRemainingSeconds, false)
        .build()
}
