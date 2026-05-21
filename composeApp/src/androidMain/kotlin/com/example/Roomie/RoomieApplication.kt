package com.example.Roomie

import android.app.Application
import com.example.Roomie.di.androidModule
import com.example.Roomie.di.initKoin
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class RoomieApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Napier for logging
        Napier.base(DebugAntilog())

        initKoin(
            platformModules = listOf(androidModule)
        ) {
            androidLogger()
            androidContext(this@RoomieApplication)
        }
    }
}
