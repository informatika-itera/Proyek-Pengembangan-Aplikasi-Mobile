package com.example.movein

import android.app.Application
import com.example.movein.core.di.androidModule
import com.example.movein.core.di.initKoin
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
                networkModule     // Mengaktifkan modul Ktor Client untuk Internet & Gemini API
                // sprint3DataModule telah dihapus karena ActivityRepository sudah terpusat di AppModule / di layer utama
            )
        ) {
            androidLogger()
            androidContext(this@NoteAIApplication)
        }
    }
}