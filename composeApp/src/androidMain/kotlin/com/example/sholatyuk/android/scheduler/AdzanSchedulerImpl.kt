package com.example.sholatyuk.android.scheduler

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.sholatyuk.domain.model.PrayerTime
import com.example.sholatyuk.domain.scheduler.AdzanScheduler
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.Calendar

class AdzanSchedulerImpl(private val context: Context) : AdzanScheduler {

    companion object {
        const val TAG = "AdzanScheduler"
        const val CHANNEL_ID = "adzan_channel"
        const val CHANNEL_NAME = "Notifikasi Adzan"
        const val EXTRA_PRAYER_NAME = "prayer_name"
        const val EXTRA_PRAYER_TIME = "prayer_time"
        const val NOTIF_ID_NEXT_PRAYER = 2001  // ID untuk notifikasi "waktu sholat berikutnya"

        private const val RC_FAJR    = 1001
        private const val RC_DHUHR   = 1002
        private const val RC_ASR     = 1003
        private const val RC_MAGHRIB = 1004
        private const val RC_ISHA    = 1005
    }

    init {
        createNotificationChannel()
    }

    override suspend fun schedule(prayerTime: PrayerTime) {
        // 1. Tampilkan notifikasi "waktu sholat berikutnya" SEKARANG saat app dibuka
        showNextPrayerNotification(prayerTime)

        // 2. Jadwalkan alarm untuk setiap waktu sholat yang belum lewat
        val prayers = listOf(
            Triple("Subuh",   prayerTime.fajr,    RC_FAJR),
            Triple("Dzuhur",  prayerTime.dhuhr,   RC_DHUHR),
            Triple("Ashar",   prayerTime.asr,     RC_ASR),
            Triple("Maghrib", prayerTime.maghrib,  RC_MAGHRIB),
            Triple("Isya",    prayerTime.isha,    RC_ISHA),
        )

        prayers.forEach { (name, timeString, requestCode) ->
            val parsedTime = parseTime(timeString) ?: return@forEach
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, parsedTime.hour)
                set(Calendar.MINUTE,      parsedTime.minute)
                set(Calendar.SECOND,      0)
                set(Calendar.MILLISECOND, 0)
            }
            if (calendar.timeInMillis > System.currentTimeMillis()) {
                setAlarm(name, timeString, requestCode, calendar.timeInMillis)
                Log.d(TAG, "✅ Alarm dijadwalkan → $name jam $timeString")
            }
        }
    }

    /**
     * Tampilkan notifikasi informatif langsung saat dipanggil.
     * Contoh: "Maghrib 17:52 · 45 menit lagi"
     */
    private fun showNextPrayerNotification(prayerTime: PrayerTime) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val nowMinutes = now.hour * 60 + now.minute

        val prayers = listOf(
            Pair("Subuh",   prayerTime.fajr),
            Pair("Dzuhur",  prayerTime.dhuhr),
            Pair("Ashar",   prayerTime.asr),
            Pair("Maghrib", prayerTime.maghrib),
            Pair("Isya",    prayerTime.isha),
        )

        // Cari waktu sholat berikutnya yang belum lewat
        val next = prayers.firstOrNull { (_, timeString) ->
            val parsed = parseTime(timeString) ?: return@firstOrNull false
            val prayerMinutes = parsed.hour * 60 + parsed.minute
            prayerMinutes > nowMinutes
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (next != null) {
            val (name, timeString) = next
            val parsed = parseTime(timeString)!!
            val prayerMinutes = parsed.hour * 60 + parsed.minute
            val diffMinutes = prayerMinutes - nowMinutes

            // Format pesan: "45 menit lagi" atau "1 jam 5 menit lagi"
            val countdownText = formatCountdown(diffMinutes)

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle("$name $timeString")               // "Maghrib 17:52"
                .setContentText("$countdownText · ${prayerTime.cityName}")  // "45 menit lagi · Serang"
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("Waktu $name pukul $timeString ($countdownText)")
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(false)   // bisa di-dismiss user
                .setAutoCancel(true)
                .build()

            notificationManager.notify(NOTIF_ID_NEXT_PRAYER, notification)
            Log.d(TAG, "🔔 Notifikasi berikutnya: $name $timeString ($countdownText)")
        } else {
            // Semua sholat hari ini sudah lewat → tampilkan info Subuh besok
            val (_, subuhTime) = prayers.first()
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle("Subuh $subuhTime")
                .setContentText("Semua sholat hari ini selesai. Subuh besok pukul $subuhTime")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()

            notificationManager.notify(NOTIF_ID_NEXT_PRAYER, notification)
        }
    }

    private fun formatCountdown(diffMinutes: Int): String {
        return when {
            diffMinutes < 60 -> "$diffMinutes menit lagi"
            else -> {
                val hours = diffMinutes / 60
                val minutes = diffMinutes % 60
                if (minutes == 0) "$hours jam lagi"
                else "$hours jam $minutes menit lagi"
            }
        }
    }

    override fun cancelAll() {
        listOf(RC_FAJR, RC_DHUHR, RC_ASR, RC_MAGHRIB, RC_ISHA).forEach { requestCode ->
            val intent = Intent(context, AdzanReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
        }
    }

    private fun setAlarm(
        prayerName: String,
        prayerTime: String,
        requestCode: Int,
        triggerAtMillis: Long
    ) {
        val intent = Intent(context, AdzanReceiver::class.java).apply {
            putExtra(EXTRA_PRAYER_NAME, prayerName)
            putExtra(EXTRA_PRAYER_TIME, prayerTime)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }

    private fun parseTime(timeString: String): LocalTime? {
        return try {
            val parts = timeString.trim().split(":")
            LocalTime(hour = parts[0].trim().toInt(), minute = parts[1].trim().toInt())
        } catch (e: Exception) {
            null
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifikasi pengingat waktu sholat"
                enableVibration(false)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// BroadcastReceiver: dipanggil AlarmManager tepat saat waktu adzan tiba
// ─────────────────────────────────────────────────────────────────────────────

class AdzanReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra(AdzanSchedulerImpl.EXTRA_PRAYER_NAME) ?: "Sholat"
        val prayerTime = intent.getStringExtra(AdzanSchedulerImpl.EXTRA_PRAYER_TIME) ?: ""
        val title = if (prayerTime.isNotEmpty()) "$prayerName $prayerTime" else prayerName

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Notifikasi yang muncul TEPAT saat adzan
        val notification = NotificationCompat.Builder(context, AdzanSchedulerImpl.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)                              // "Maghrib 17:52"
            .setContentText("Sudah masuk waktu $prayerName. Yuk, segera tunaikan sholat! 🕌")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(prayerName.hashCode(), notification)
        Log.d(AdzanSchedulerImpl.TAG, "🔔 Adzan tiba → $title")
    }
}