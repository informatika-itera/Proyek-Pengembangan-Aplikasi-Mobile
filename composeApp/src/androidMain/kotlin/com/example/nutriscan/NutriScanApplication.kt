package com.example.nutriscan

import android.app.Application
import com.example.nutriscan.core.di.androidModule
import com.example.nutriscan.core.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

/**
 * Android Application class.
 * Menginisialisasi Koin DI saat aplikasi pertama kali dibuat.
 */
class NutriScanApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(
            platformModules = listOf(androidModule)
        ) {
            androidLogger()
            androidContext(this@NutriScanApplication)
        }
    }
}