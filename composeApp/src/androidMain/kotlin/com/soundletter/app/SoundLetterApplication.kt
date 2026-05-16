package com.soundletter.app

import android.app.Application
import com.soundletter.app.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class SoundLetterApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@SoundLetterApplication)
            modules(appModule)
        }
    }
}
