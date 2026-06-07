package com.studyhub.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.studyhub.domain.model.TaskStatus
import com.studyhub.domain.repository.NotifHistoryRepository
import com.studyhub.domain.usecase.task.UpdateTaskStatusUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ReminderBroadcastReceiver : BroadcastReceiver(), KoinComponent {

    private val notifHistoryRepository: NotifHistoryRepository by inject()
    private val updateTaskStatusUseCase: UpdateTaskStatusUseCase by inject()

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "ACTION_SHOW_REMINDER" -> {
                val taskId = intent.getStringExtra("taskId") ?: return
                val taskTitle = intent.getStringExtra("taskTitle") ?: return
                val taskSubject = intent.getStringExtra("taskSubject") ?: "Mata Kuliah"
                val aiReason = intent.getStringExtra("aiReason") ?: "Deadline mendekat"

                // Show notification
                val notif = buildReminderNotification(
                    context, taskId, taskTitle,
                    taskSubject, aiReason
                )
                val manager = context.getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager

                manager.notify(
                    NotificationIds.BASE_REMINDER + taskId.hashCode(),
                    notif
                )

                // Save to history
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        println("ReminderBroadcastReceiver: Saving reminder to history - $taskTitle")
                        notifHistoryRepository.addToHistory(
                            taskId, taskTitle,
                            taskSubject, aiReason
                        )
                    } catch (e: Exception) {
                        println("ReminderBroadcastReceiver: Error saving to history: ${e.message}")
                    }
                }
            }

            "ACTION_MARK_DONE" -> {
                val taskId = intent.getStringExtra("taskId") ?: return
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        updateTaskStatusUseCase(taskId, TaskStatus.DONE)
                    } catch (e: Exception) { }
                }
            }
        }
    }
}
