package com.example.sholatyuk

import android.app.Application
import com.example.sholatyuk.core.di.initKoin
import com.example.sholatyuk.core.util.DatabaseDriverFactory
import com.example.sholatyuk.core.location.LocationService
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.dsl.module

class SholatYukApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val androidModule = module {
            single { DatabaseDriverFactory(androidContext()) }
            single { LocationService() } // <-- Kurungnya dikosongkan
        }

        initKoin(
            platformModules = listOf(androidModule)
        ) {
            androidLogger()
            androidContext(this@SholatYukApplication)
        }
    }
}