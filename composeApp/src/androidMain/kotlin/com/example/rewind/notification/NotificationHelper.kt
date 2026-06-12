package com.example.rewind.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.rewind.MainActivity
import com.example.rewind.R

/**
 * Helper class untuk mengelola Notification Channel dan mengirim notifikasi.
 *
 * Menggunakan NotificationCompat untuk backward compatibility.
 */
class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "rewind_watch_reminder"
        const val CHANNEL_NAME = "Reminder Nonton"
        const val CHANNEL_DESCRIPTION = "Notifikasi pengingat untuk melanjutkan film yang sedang ditonton"
        const val NOTIFICATION_ID = 1001
    }

    /**
     * Membuat Notification Channel (required untuk Android 8.0+).
     * Aman dipanggil berulang kali — sistem akan skip jika channel sudah ada.
     */
    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Menampilkan notifikasi reminder nonton.
     *
     * @param title Judul notifikasi
     * @param body Isi notifikasi
     */
    fun showWatchReminderNotification(title: String, body: String) {
        // Intent untuk membuka app saat notifikasi di-tap
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
