package com.soundletter.app

import android.app.Application
import com.soundletter.app.di.initKoin
import com.soundletter.app.di.platformModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level

class SoundLetterApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidLogger(Level.INFO)
            androidContext(this@SoundLetterApplication)
            modules(platformModule)
        }
    }
}