package com.studymate.core.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.studymate.R
import java.util.concurrent.TimeUnit

class NotificationHelper(private val context: Context) : NotificationScheduler {
    companion object {
        const val CHANNEL_ID = "studymate_reminders"
        const val CHANNEL_NAME = "StudyMate Reminders"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi untuk deadline dan pengingat belajar"
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun scheduleReminder(id: Long, title: String, dueDate: Long) {
        val now = System.currentTimeMillis()
        
        // Days to notify: 3, 2, and 1 day before
        val triggerDays = listOf(3, 2, 1)
        
        triggerDays.forEach { daysBefore ->
            val triggerTime = dueDate - (daysBefore * 24 * 60 * 60 * 1000L)
            val delay = triggerTime - now
            
            if (delay > 0) {
                val data = workDataOf(
                    "title" to title,
                    "message" to "Deadline $daysBefore hari lagi: $title",
                    "id" to id.toInt() + daysBefore
                )

                val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .setInputData(data)
                    .build()

                WorkManager.getInstance(context).enqueue(workRequest)
            }
        }
    }
}

class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val title = inputData.getString("title") ?: "StudyMate"
        val message = inputData.getString("message") ?: "Ada pengingat untukmu!"
        val id = inputData.getInt("id", 0)

        val builder = NotificationCompat.Builder(applicationContext, NotificationHelper.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Placeholder
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            androidx.core.content.ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            manager.notify(id, builder.build())
        }

        return Result.success()
    }
}
