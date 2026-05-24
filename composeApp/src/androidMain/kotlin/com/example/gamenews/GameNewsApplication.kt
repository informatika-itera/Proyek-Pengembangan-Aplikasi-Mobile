package com.example.gamenews

import android.app.Application
import com.example.gamenews.core.di.initKoin
import com.example.gamenews.core.di.androidModule
import org.koin.android.ext.koin.androidContext

class GameNewsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(
            platformModules = listOf(androidModule)
        ) {
            androidContext(androidContext = this@GameNewsApplication)
        }
    }
}