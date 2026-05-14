package com.example.Roomie

import android.app.Application
import com.example.Roomie.di.androidModule
import com.example.Roomie.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class RoomieApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(
            platformModules = listOf(androidModule)
        ) {
            androidLogger()
            androidContext(this@RoomieApplication)
        }
    }
}
