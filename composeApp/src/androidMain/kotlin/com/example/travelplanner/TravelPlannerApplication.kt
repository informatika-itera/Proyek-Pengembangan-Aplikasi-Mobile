package com.example.travelplanner

import android.app.Application
import com.example.travelplanner.core.di.initKoin
import com.example.travelplanner.core.util.DatabaseDriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.dsl.module

val androidModule = module {
    // get() secara otomatis akan mengambil Context Android yang disuntikkan di bawah
    single { DatabaseDriverFactory(get()) }
}

class TravelPlannerApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidLogger()
            androidContext(this@TravelPlannerApplication)
            modules(androidModule)
        }
    }
}