package com.example.pantaujompo

import android.app.Application
import com.example.pantaujompo.core.di.androidModule
import com.example.pantaujompo.core.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

/**
 * Android Application class
 * 
 * Entry point untuk inisialisasi app-wide dependencies (Koin dll).
 */
class PantauJompoApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Koin DI
        initKoin(
            platformModules = listOf(androidModule)
        ) {
            androidLogger()
            androidContext(this@PantauJompoApplication)
        }
    }
}
