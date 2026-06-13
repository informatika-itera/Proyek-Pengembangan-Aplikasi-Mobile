package com.example.foodsaver

import android.app.Application
import com.example.foodsaver.core.injection.androidModule
import com.example.foodsaver.core.injection.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class FoodSaverApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        initKoin(
            platformModules = listOf(androidModule)
        ) {
            androidLogger()
            androidContext(this@FoodSaverApplication)
        }
    }
}
