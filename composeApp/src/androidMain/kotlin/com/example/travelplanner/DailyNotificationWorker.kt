package com.example.travelplanner

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class DailyNotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        showNotification()
        return Result.success()
    }

    private fun showNotification() {
        val channelId = "daily_travel_reminder"
        val notificationId = 1

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Travel Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily reminder for your travel plans"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val sharedPref = context.getSharedPreferences("TravelPlannerPrefs", Context.MODE_PRIVATE)
        val isEnglish = sharedPref.getBoolean("isEnglish", false)

        val messagesId = listOf(
            "Sudah ada rencana liburan belum? Yuk rencanakan sekarang!",
            "Jangan lupa rehat sejenak. Destinasi impian menantimu!",
            "Waktunya healing! Cek rencana perjalananmu hari ini.",
            "Dunia ini luas lho, yuk mulai buat rencana traveling seru!",
            "Kapan terakhir kali kamu jalan-jalan? Yuk mulai kemas barangmu!"
        )

        val messagesEn = listOf(
            "Got any vacation plans yet? Let's plan it now!",
            "Don't forget to take a break. Your dream destination awaits!",
            "Time to heal! Check out your travel plans for today.",
            "The world is vast, let's start making fun travel plans!",
            "When was the last time you went traveling? Start packing!"
        )
        
        val message = if (isEnglish) messagesEn.random() else messagesId.random()
        val title = if (isEnglish) "Time to Travel! ✈️" else "Waktunya Traveling! ✈️"

        val builder = NotificationCompat.Builder(context, channelId)
            // Icon needs to exist, android.R.drawable.ic_dialog_map is a built-in icon we can use
            // or we could use the app icon, but sometimes it doesn't look good if it's not a silhouette.
            // Using a standard built-in icon for now:
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(notificationId, builder.build())
    }
}
