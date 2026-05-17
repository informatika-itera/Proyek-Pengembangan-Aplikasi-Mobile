package com.example.masakuy.data

import android.app.Application
import com.example.masakuy.core.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MasakuyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MasakuyApplication)
            modules(appModule())
        }
    }
}