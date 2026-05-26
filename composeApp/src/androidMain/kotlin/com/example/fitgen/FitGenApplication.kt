package com.example.fitgen

import android.app.Application
import com.example.fitgen.core.di.androidModule
import com.example.fitgen.core.di.initKoin
import org.koin.android.ext.koin.androidContext

/**
 * Android Application class for FitGen.
 * Entry point for app-wide dependency injection.
 */
class FitGenApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initKoin(platformModules = listOf(androidModule)) {
            androidContext(this@FitGenApplication)
        }
    }
}
