package com.dailybliss.app

import android.app.Application
import com.dailybliss.app.core.di.androidModule
import com.dailybliss.app.core.di.initKoin
import com.dailybliss.app.presentation.util.FileStorage
import org.koin.android.ext.koin.androidContext

/**
 * Android Application class
 * 
 * Entry point untuk inisialisasi app-wide dependencies.
 */
class DailyBlissApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize File Storage
        FileStorage.init(this)
        
        // Initialize Koin DI
        initKoin(
            platformModules = listOf(androidModule)
        ) {
            androidContext(this@DailyBlissApplication)
        }
    }
}
