package com.example.synesthesia

import android.app.Application
import com.example.synesthesia.core.di.androidModule
import com.example.synesthesia.core.di.initKoin
import com.example.synesthesia.core.notification.NotificationHelper
import com.example.synesthesia.core.notification.JournalReminderWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.ExistingPeriodicWorkPolicy
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import java.util.concurrent.TimeUnit

/**
 * Android Application class
 * 
 * Entry point untuk inisialisasi app-wide dependencies.
 */
class NoteAIApplication : Application() {
    
    companion object {
        lateinit var instance: NoteAIApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // Initialize Koin DI
        initKoin(
            platformModules = listOf(androidModule)
        ) {
            androidLogger()
            androidContext(this@NoteAIApplication)
        }

        setupNotificationReminders()
    }

    private fun setupNotificationReminders() {
        NotificationHelper(this).createNotificationChannel()
        
        val workRequest = PeriodicWorkRequestBuilder<JournalReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(1, TimeUnit.HOURS) // Simple delay for demo
            .build()
            
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "journal_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
