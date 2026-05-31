package com.example.noteai

import android.app.Application
import com.example.noteai.core.di.androidModule
import com.example.noteai.core.di.initKoin
import com.movein.di.networkModule // Mengimpor Ktor Client
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

/**
 * Android Application class
 * Entry point untuk inisialisasi app-wide dependencies.
 */
class NoteAIApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Koin DI
        initKoin(
            platformModules = listOf(
                androidModule,
                networkModule,     // Mengaktifkan modul Ktor Client untuk Internet & Gemini API
                sprint3DataModule  // Mengaktifkan modul data layer (Repository) baru buatan Raisya
            )
        ) {
            androidLogger()
            androidContext(this@NoteAIApplication)
        }
    }
}