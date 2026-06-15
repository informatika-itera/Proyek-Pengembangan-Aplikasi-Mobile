package com.studyhub.notification

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.studyhub.domain.model.NotifType
import com.studyhub.domain.repository.NotifHistoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PomodoroBroadcastReceiver : BroadcastReceiver(), KoinComponent {

    private val notifHistoryRepository: NotifHistoryRepository by inject()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        println("PomodoroBroadcastReceiver: Received action = $action")
        
        val pendingResult = goAsync()
        
        scope.launch {
            try {
                handleAction(context, action, intent)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun handleAction(context: Context, action: String, intent: Intent) {
        when (action) {
            "ACTION_FOCUS_DONE" -> {
                val sessionNumber = intent.getIntExtra("sessionNumber", 1)
                val totalSessions = intent.getIntExtra("totalSessions", 4)
                val linkedTaskTitle = intent.getStringExtra("linkedTaskTitle")

                cancelOngoingNotification(context)

                val notif = buildFocusDoneNotification(
                    context, sessionNumber, totalSessions, linkedTaskTitle
                )
                showNotification(context, NotificationIds.POMODORO_FOCUS_DONE, notif)

                saveToHistory(
                    "Sesi Fokus $sessionNumber/$totalSessions",
                    "Pomodoro",
                    "Sesi fokus selesai!"
                )

                LocalBroadcastManager.getInstance(context).sendBroadcast(Intent("POMODORO_FOCUS_DONE"))
            }

            "ACTION_BREAK_DONE" -> {
                val isLongBreak = intent.getBooleanExtra("isLongBreak", false)
                val linkedTaskTitle = intent.getStringExtra("linkedTaskTitle")

                cancelOngoingNotification(context)

                val notif = buildBreakDoneNotification(context, isLongBreak, linkedTaskTitle)
                showNotification(context, NotificationIds.POMODORO_BREAK_DONE, notif)

                saveToHistory(
                    if (isLongBreak) "Istirahat Panjang Selesai" else "Istirahat Pendek Selesai",
                    "Pomodoro",
                    if (isLongBreak) "Siap mulai lagi!" else "Lanjut fokus!"
                )

                LocalBroadcastManager.getInstance(context).sendBroadcast(Intent("POMODORO_BREAK_DONE"))
            }

            "ACTION_UPDATE_ONGOING" -> {
                val phase = intent.getStringExtra("phase") ?: return
                val timeRemaining = intent.getIntExtra("timeRemaining", 0)
                val sessionNumber = intent.getIntExtra("sessionNumber", 1)
                val totalSessions = intent.getIntExtra("totalSessions", 4)
                val linkedTaskTitle = intent.getStringExtra("linkedTaskTitle")

                val notif = buildPomodoroOngoingNotification(
                    context, phase, timeRemaining, sessionNumber, totalSessions, linkedTaskTitle
                )
                showNotification(context, NotificationIds.POMODORO_ONGOING, notif)
            }

            "ACTION_PAUSE_POMODORO" -> {
                LocalBroadcastManager.getInstance(context).sendBroadcast(Intent("POMODORO_PAUSE"))
            }

            "ACTION_STOP_POMODORO" -> {
                cancelOngoingNotification(context)
                LocalBroadcastManager.getInstance(context).sendBroadcast(Intent("POMODORO_STOP"))
            }

            "ACTION_START_BREAK" -> {
                LocalBroadcastManager.getInstance(context).sendBroadcast(Intent("POMODORO_START_BREAK"))
            }

            "ACTION_START_FOCUS" -> {
                LocalBroadcastManager.getInstance(context).sendBroadcast(Intent("POMODORO_START_FOCUS"))
            }
            
            "ACTION_TEST_NOTIF" -> {
                println("PomodoroBroadcastReceiver: Test notification triggered")
                val notif = buildReminderNotification(
                    context, "test_id", "Test Notifikasi", "System", "Hanya uji coba"
                )
                showNotification(context, 9999, notif)
                saveToHistory("Test Notif", "System", "Berhasil")
            }
        }
    }

    private fun showNotification(context: Context, id: Int, notification: Notification) {
        try {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(id, notification)
            println("PomodoroBroadcastReceiver: Notification $id shown")
        } catch (e: Exception) {
            println("PomodoroBroadcastReceiver: Error showing notification: ${e.message}")
        }
    }

    private fun cancelOngoingNotification(context: Context) {
        try {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.cancel(NotificationIds.POMODORO_ONGOING)
        } catch (e: Exception) { }
    }

    private suspend fun saveToHistory(title: String, subject: String, reason: String) {
        try {
            notifHistoryRepository.addToHistory(
                taskId = "pomodoro_${System.currentTimeMillis()}",
                taskTitle = title,
                taskSubject = subject,
                aiReason = reason,
                type = NotifType.POMODORO
            )
            println("PomodoroBroadcastReceiver: History saved")
        } catch (e: Exception) {
            println("PomodoroBroadcastReceiver: Error saving history: ${e.message}")
        }
    }
}
