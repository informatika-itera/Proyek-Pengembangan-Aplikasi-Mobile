package com.studyhub

import android.app.Application
import com.studyhub.core.di.androidModule
import com.studyhub.core.di.initKoin
import com.studyhub.notification.createNotificationChannels
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

/**
 * Android Application class
 * 
 * Entry point untuk inisialisasi app-wide dependencies.
 */
class StudyHubApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Create notification channels
        createNotificationChannels(this)
        
        // Initialize Koin DI
        initKoin(
            platformModules = listOf(androidModule)
        ) {
            androidLogger()
            androidContext(this@StudyHubApplication)
        }
    }
}

