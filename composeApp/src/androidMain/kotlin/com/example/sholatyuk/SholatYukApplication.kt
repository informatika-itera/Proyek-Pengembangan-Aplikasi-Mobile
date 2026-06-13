package com.example.sholatyuk

import android.app.Application
import com.example.sholatyuk.core.di.androidModule
import com.example.sholatyuk.core.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class SholatYukApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin(
            platformModules = listOf(androidModule) // ← pakai androidModule dari AndroidModule.kt
        ) {
            androidLogger()
            androidContext(this@SholatYukApplication)
        }
    }
}