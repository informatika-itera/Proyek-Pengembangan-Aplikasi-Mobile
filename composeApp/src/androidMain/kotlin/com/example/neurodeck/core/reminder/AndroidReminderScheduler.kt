package com.example.neurodeck.core.reminder

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.neurodeck.domain.reminder.ReminderScheduler
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Implementasi [ReminderScheduler] untuk Android pakai WorkManager.
 *
 * Strategi: enqueue OneTimeWorkRequest dengan initialDelay sampai jam:menit
 * berikutnya. Saat Worker jalan, ia menampilkan notifikasi LALU menjadwalkan
 * dirinya lagi untuk besok (lihat [ReminderWorker]). Pakai unique work supaya
 * tidak ada jadwal dobel.
 */
class AndroidReminderScheduler(
    private val context: Context,
) : ReminderScheduler {

    override fun schedule(hour: Int, minute: Int) {
        val delay = computeInitialDelayMillis(hour, minute)
        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf(KEY_HOUR to hour, KEY_MINUTE to minute))
            .addTag(WORK_NAME)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, request)
    }

    override fun cancel() {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    companion object {
        const val WORK_NAME = "neurodeck_daily_reminder"
        const val KEY_HOUR = "key_reminder_hour"
        const val KEY_MINUTE = "key_reminder_minute"

        /**
         * Hitung delay (ms) dari sekarang sampai [hour]:[minute] berikutnya.
         * Kalau jam target hari ini sudah lewat, jadwalkan untuk besok.
         */
        fun computeInitialDelayMillis(hour: Int, minute: Int): Long {
            val now = Calendar.getInstance()
            val target = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            if (target.timeInMillis <= now.timeInMillis) {
                target.add(Calendar.DAY_OF_YEAR, 1)
            }
            return target.timeInMillis - now.timeInMillis
        }
    }
}