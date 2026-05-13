package com.example.gamenews

import android.app.Application
import com.example.gamenews.core.di.initKoin
import org.koin.android.ext.koin.androidContext

class GameNewsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@GameNewsApplication)
        }
    }
}