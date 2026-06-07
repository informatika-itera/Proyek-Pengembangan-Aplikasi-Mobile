package com.kelazzz.app.core.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.kelazzz.app.domain.model.Jadwal
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AndroidJadwalNotificationScheduler(
    private val context: Context
) : JadwalNotificationScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override suspend fun schedule(jadwal: Jadwal) {
        val offsetMinutes = jadwal.reminderOffsetMinutes ?: run {
            cancel(jadwal.id)
            return
        }

        val scheduleAtMillis = parseScheduleMillis(jadwal) ?: run {
            cancel(jadwal.id)
            return
        }
        val triggerAtMillis = scheduleAtMillis - offsetMinutes * 60_000L

        if (triggerAtMillis <= System.currentTimeMillis()) {
            Log.w(TAG, "Reminder skipped because trigger time is in the past: jadwalId=${jadwal.id}")
            cancel(jadwal.id)
            return
        }

        val pendingIntent = createPendingIntent(jadwal, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )
        Log.d(TAG, "Reminder scheduled: jadwalId=${jadwal.id}, triggerAtMillis=$triggerAtMillis")
    }

    override suspend fun cancel(jadwalId: Long) {
        val intent = Intent(context, JadwalNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            jadwalId.toNotificationRequestCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d(TAG, "Reminder cancelled: jadwalId=$jadwalId")
        }
    }

    private fun createPendingIntent(jadwal: Jadwal, flags: Int): PendingIntent {
        val intent = Intent(context, JadwalNotificationReceiver::class.java).apply {
            putExtra(JadwalNotificationReceiver.EXTRA_JADWAL_ID, jadwal.id)
            putExtra(JadwalNotificationReceiver.EXTRA_TITLE, jadwal.judul)
            putExtra(JadwalNotificationReceiver.EXTRA_DESCRIPTION, jadwal.deskripsi)
            putExtra(JadwalNotificationReceiver.EXTRA_DATE, jadwal.tanggal)
            putExtra(JadwalNotificationReceiver.EXTRA_TIME, jadwal.waktu)
            putExtra(JadwalNotificationReceiver.EXTRA_KIND, jadwal.jenis.displayName)
            putExtra(JadwalNotificationReceiver.EXTRA_OFFSET_MINUTES, jadwal.reminderOffsetMinutes ?: 0L)
        }

        return PendingIntent.getBroadcast(
            context,
            jadwal.id.toNotificationRequestCode(),
            intent,
            flags or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun parseScheduleMillis(jadwal: Jadwal): Long? {
        val startTimeText = jadwal.waktu.substringBefore("-").trim()
        val date = runCatching { DATE_FORMAT.parse(jadwal.tanggal.trim()) }.getOrNull() ?: return null
        val timeParts = startTimeText.split(":")
        val hour = timeParts.getOrNull(0)?.toIntOrNull() ?: return null
        val minute = timeParts.getOrNull(1)?.toIntOrNull() ?: return null
        if (hour !in 0..23 || minute !in 0..59) return null

        return Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    companion object {
        private const val TAG = "JadwalReminder"
        private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    }
}

private fun Long.toNotificationRequestCode(): Int {
    return (this and 0x7fffffff).toInt()
}
