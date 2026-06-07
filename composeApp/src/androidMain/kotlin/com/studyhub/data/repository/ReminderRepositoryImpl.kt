package com.studyhub.data.repository

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.studyhub.domain.model.ReminderInfo
import com.studyhub.domain.repository.ReminderRepository
import com.studyhub.domain.repository.TaskRepository
import com.studyhub.data.local.ReminderDataSource
import com.studyhub.notification.ReminderBroadcastReceiver
import com.studyhub.core.util.currentTimeMillis
import kotlinx.coroutines.flow.first

class ReminderRepositoryImpl(
    private val context: Context,
    private val reminderDataSource: ReminderDataSource,
    private val taskRepository: TaskRepository
) : ReminderRepository {

    private val alarmManager = context.getSystemService(
        Context.ALARM_SERVICE
    ) as AlarmManager

    override suspend fun scheduleReminder(
        taskId: String,
        scheduledAt: Long,
        aiReason: String,
        taskTitle: String,
        taskSubject: String
    ) {
        // Skip if reminder is in the past
        if (scheduledAt <= currentTimeMillis()) return

        val intent = Intent(
            context, ReminderBroadcastReceiver::class.java
        ).apply {
            action = "ACTION_SHOW_REMINDER"
            putExtra("taskId", taskId)
            putExtra("taskTitle", taskTitle)
            putExtra("taskSubject", taskSubject)
            putExtra("aiReason", aiReason)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
            PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        scheduledAt,
                        pendingIntent
                    )
                } else {
                    // Fallback to inexact alarm
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        scheduledAt,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    scheduledAt,
                    pendingIntent
                )
            }
            reminderDataSource.upsert(taskId, scheduledAt, aiReason)
        } catch (e: SecurityException) {
            // Permission not granted — save to DB only
            reminderDataSource.upsert(taskId, scheduledAt, aiReason)
        } catch (e: Exception) {
            reminderDataSource.upsert(taskId, scheduledAt, aiReason)
        }
    }

    override suspend fun cancelReminder(taskId: String) {
        val intent = Intent(
            context, ReminderBroadcastReceiver::class.java
        )
        val pendingIntent = PendingIntent.getBroadcast(
            context, taskId.hashCode(), intent,
            PendingIntent.FLAG_NO_CREATE or
            PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.let {
            try { alarmManager.cancel(it) } catch (e: Exception) { }
        }
        reminderDataSource.deactivate(taskId)
    }

    override suspend fun cancelAllReminders() {
        val active = reminderDataSource.getAllActive()
        active.forEach { cancelReminder(it.taskId) }
        reminderDataSource.deactivateAll()
    }

    override suspend fun getReminderForTask(
        taskId: String
    ) = reminderDataSource.getByTaskId(taskId)

    override suspend fun getAllActiveReminders() =
        reminderDataSource.getAllActive()

    override suspend fun rescheduleAllOnBoot() {
        val active = reminderDataSource.getAllActive()
        active.forEach { reminder ->
            if (reminder.scheduledAt > currentTimeMillis()) {
                val task = taskRepository.getTaskById(reminder.taskId).first()
                task?.let {
                    scheduleReminder(
                        reminder.taskId,
                        reminder.scheduledAt,
                        reminder.aiReason,
                        it.title,
                        it.subject
                    )
                }
            } else {
                reminderDataSource.deactivate(reminder.taskId)
            }
        }
    }
}
