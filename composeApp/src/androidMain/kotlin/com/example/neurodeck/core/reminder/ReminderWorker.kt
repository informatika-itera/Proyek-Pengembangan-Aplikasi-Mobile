package com.example.neurodeck.core.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.neurodeck.MainActivity
import com.example.neurodeck.domain.repository.CardRepository
import kotlinx.datetime.Clock
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Worker yang menampilkan notifikasi pengingat belajar harian, lalu menjadwalkan
 * dirinya lagi untuk hari berikutnya.
 *
 * Akses database (jumlah kartu due) lewat Koin via [KoinComponent].
 */
class ReminderWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params), KoinComponent {

    private val cardRepository: CardRepository by inject()

    override suspend fun doWork(): Result {
        val dueCount = try {
            cardRepository.countAllDueCards(Clock.System.now())
        } catch (e: Exception) {
            0L
        }

        showNotification(dueCount)

        // Jadwalkan ulang untuk besok di jam yang sama
        val hour = inputData.getInt(AndroidReminderScheduler.KEY_HOUR, 19)
        val minute = inputData.getInt(AndroidReminderScheduler.KEY_MINUTE, 0)
        AndroidReminderScheduler(applicationContext).schedule(hour, minute)

        return Result.success()
    }

    private fun showNotification(dueCount: Long) {
        val ctx = applicationContext
        ensureChannel(ctx)

        val text = if (dueCount > 0) {
            "Ada $dueCount kartu siap dipelajari. Yuk lanjutkan belajarmu! 🔥"
        } else {
            "Jaga streak-mu — buka NeuroDeck dan belajar sebentar. 🔥"
        }

        val intent = Intent(ctx, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            ctx,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

        val notification = NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setSmallIcon(ctx.applicationInfo.icon)
            .setContentTitle("Waktunya Belajar! 🔥")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        // Android 13+ butuh izin POST_NOTIFICATIONS — kalau belum di-grant, skip.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                ctx,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }

        NotificationManagerCompat.from(ctx).notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "neurodeck_reminder_channel"
        const val NOTIFICATION_ID = 1001

        /** Buat notification channel (idempotent — aman dipanggil berkali-kali). */
        fun ensureChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Pengingat Belajar",
                    NotificationManager.IMPORTANCE_DEFAULT,
                ).apply {
                    description = "Pengingat harian untuk belajar flashcard"
                }
                context.getSystemService(NotificationManager::class.java)
                    ?.createNotificationChannel(channel)
            }
        }
    }
}