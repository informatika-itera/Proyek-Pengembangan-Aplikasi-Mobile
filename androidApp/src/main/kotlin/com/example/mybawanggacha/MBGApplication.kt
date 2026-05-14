package com.example.mybawanggacha

import android.app.Application
import com.example.mybawanggacha.core.di.androidModule
import com.example.mybawanggacha.core.di.initKoin
import com.example.mybawanggacha.core.network.ApiConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

/**
 * Android Application class
 * * Entry point untuk inisialisasi app-wide dependencies.
 */
class MBGApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ApiConfig.initialize(BuildConfig.GEMINI_API_KEY)

        initKoin(
            platformModules = listOf(androidModule)
        ) {
            androidLogger()
            androidContext(this@MBGApplication)
        }
    }
}