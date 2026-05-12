package com.example.pantaujompo

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class PantauJompoApplication : Application() {

    // Ini jalur pintas Context-nya!
    companion object {
        lateinit var appContext: Application
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
        startKoin {
            androidLogger()
            androidContext(this@PantauJompoApplication)
        }
    }
}