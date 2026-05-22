package com.itera.news

import android.app.Application
import com.itera.news.core.di.platformModule
import com.itera.news.core.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class NewsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@NewsApplication)
            modules(sharedModule, platformModule)
        }
    }
}