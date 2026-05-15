package com.example.edumate

import android.app.Application
import com.example.edumate.core.di.androidModule
import com.example.edumate.core.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level

class NoteAIApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Memasukkan androidModule sangat krusial agar Database tidak crash
        initKoin(platformModules = listOf(androidModule)) {
            androidLogger(Level.ERROR)
            androidContext(this@NoteAIApplication)
        }
    }
}