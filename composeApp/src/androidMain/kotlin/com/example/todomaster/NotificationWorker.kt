package com.example.todomaster

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.todomaster.domain.repository.TaskRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NotificationWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val repository: TaskRepository by inject()

    override suspend fun doWork(): Result {
        try {
            val tasks = repository.getAllTasks().firstOrNull() ?: emptyList()

            val tasksDueTomorrow = tasks.filter { !it.isCompleted && isDueTomorrow(it.dueDate) }

            if (tasksDueTomorrow.isNotEmpty()) {
                val taskTitles = tasksDueTomorrow.joinToString(", ") { it.title }
                showNotification(
                    title = "Deadline Besok! (${tasksDueTomorrow.size} Tugas)",
                    message = taskTitles
                )
            }
            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }

    private fun isDueTomorrow(dueDateMillis: Long?): Boolean {
        if (dueDateMillis == null) return false

        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val tomorrow = today.plus(1, kotlinx.datetime.DateTimeUnit.DAY)

        val dueDate = Instant.fromEpochMilliseconds(dueDateMillis).toLocalDateTime(TimeZone.currentSystemDefault()).date
        return tomorrow == dueDate
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "TODOMASTER_CHANNEL"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Pengingat Tugas", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}