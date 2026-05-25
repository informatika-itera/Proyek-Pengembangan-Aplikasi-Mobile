package com.studymate

import android.app.Application
import com.studymate.core.di.initKoin
import com.studymate.core.util.DatabaseDriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.dsl.module

class StudyMateApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@StudyMateApp)
            modules(module {
                single { DatabaseDriverFactory(get()).createDriver() }
            })
        }
    }
}
