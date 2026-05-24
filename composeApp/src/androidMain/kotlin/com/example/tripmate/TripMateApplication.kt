package com.example.tripmate

import android.app.Application
import com.example.tripmate.core.di.androidModule
import com.example.tripmate.core.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class TripMateApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Kembalikan suntikan androidModule ke dalam initKoin
        initKoin(
            platformModules = listOf(androidModule)
        ) {
            androidLogger()
            androidContext(this@TripMateApplication)
        }
    }
}