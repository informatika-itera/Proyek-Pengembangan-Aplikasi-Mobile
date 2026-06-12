package com.example.rewind

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.rewind.core.di.androidModule
import com.example.rewind.core.di.initKoin
import com.example.rewind.notification.NotificationHelper
import com.example.rewind.notification.WatchReminderWorker
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import java.util.concurrent.TimeUnit

/**
 * Android Application class
 * 
 * Entry point untuk inisialisasi app-wide dependencies.
 */
class RewindApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Koin DI
        initKoin(
            platformModules = listOf(androidModule)
        ) {
            androidLogger()
            androidContext(this@RewindApplication)
        }

        // Initialize Notification Channel
        val notificationHelper = NotificationHelper(this)
        notificationHelper.createNotificationChannel()

        // Schedule daily watch reminder notification
        scheduleWatchReminder()
    }

    /**
     * Menjadwalkan WorkManager untuk mengirim reminder nonton harian.
     * 
     * Menggunakan PeriodicWorkRequest dengan interval 24 jam.
     * ExistingPeriodicWorkPolicy.KEEP memastikan tidak ada duplikasi
     * jika app di-restart berulang kali.
     */
    private fun scheduleWatchReminder() {
        val workRequest = PeriodicWorkRequestBuilder<WatchReminderWorker>(
            24, TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            WatchReminderWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
