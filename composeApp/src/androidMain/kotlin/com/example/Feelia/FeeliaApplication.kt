package com.example.Feelia

import android.app.Application
import com.example.Feelia.core.di.androidModule
import com.example.Feelia.core.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

/**
 * Android Application class
 * 
 * Entry point untuk inisialisasi app-wide dependencies.
 */
class FeeliaApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Koin DI
        initKoin(
            platformModules = listOf(androidModule)
        ) {
            androidLogger()
            androidContext(this@FeeliaApplication)
        }
    }
}
