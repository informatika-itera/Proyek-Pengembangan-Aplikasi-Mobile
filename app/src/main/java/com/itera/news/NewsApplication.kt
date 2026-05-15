package com.itera.news

import android.app.Application
import com.itera.news.di.databaseModule // Import ini
import com.itera.news.di.networkModule
import com.itera.news.di.repositoryModule
import com.itera.news.di.useCaseModule
import com.itera.news.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class NewsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@NewsApplication)
            // Tambahkan databaseModule di sini
            modules(databaseModule, networkModule, repositoryModule, useCaseModule, viewModelModule)
        }
    }
}