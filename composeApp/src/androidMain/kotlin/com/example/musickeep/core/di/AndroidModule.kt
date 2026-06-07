package com.example.musickeep.core.di

import com.example.musickeep.core.util.DatabaseDriverFactory
import com.example.musickeep.data.local.datastore.DataStoreFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import com.example.musickeep.BuildConfig

val androidModule = module {
    single { DatabaseDriverFactory(androidContext()) }
    single { DataStoreFactory(androidContext()) }
    // Menggunakan API Key dari local.properties (Aman)
    single<String>(qualifier = org.koin.core.qualifier.named("GEMINI_API_KEY")) { 
        BuildConfig.GEMINI_API_KEY
    }
}
