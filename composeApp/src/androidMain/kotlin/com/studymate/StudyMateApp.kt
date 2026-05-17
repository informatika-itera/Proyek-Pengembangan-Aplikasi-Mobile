package com.studymate

import android.app.Application
import com.studymate.core.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class StudyMateApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@StudyMateApp)
        }
    }
}
