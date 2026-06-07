package com.example.todomaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import androidx.work.OneTimeWorkRequestBuilder

/**
 * Android MainActivity
 * 
 * Entry point untuk Android app.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupDailyNotification(this)
        
        // Enable edge-to-edge display
        enableEdgeToEdge()
        
        setContent {
            App()
        }
    }
}

private fun setupDailyNotification(context: Context) {
    val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>().build()
    WorkManager.getInstance(context).enqueue(workRequest)
}

