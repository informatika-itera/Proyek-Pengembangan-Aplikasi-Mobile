package com.kelazzz.app

import android.app.Application
import com.kelazzz.app.core.di.androidModule
import com.kelazzz.app.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

/**
 * Android Application class — KelazZz
 * 
 * Entry point untuk inisialisasi app-wide dependencies.
 */
class KelazZzApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Koin DI
        initKoin(
            platformModules = listOf(androidModule)
        ) {
            androidLogger()
            androidContext(this@KelazZzApplication)
        }
    }
}
