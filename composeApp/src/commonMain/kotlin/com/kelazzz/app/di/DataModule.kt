package com.kelazzz.app.di

import com.kelazzz.app.core.network.HttpClientFactory
import com.kelazzz.app.core.util.DatabaseDriverFactory
import com.kelazzz.app.data.local.KelazZzDatabase
import com.kelazzz.app.data.local.datastore.DataStoreFactory
import com.kelazzz.app.data.local.datastore.UserPreferences
import com.kelazzz.app.data.local.datastore.create
import com.kelazzz.app.data.remote.gemini.GeminiService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Data Layer — Koin DI Module
 * 
 * Menyediakan dependencies untuk data layer:
 * - Network (HttpClient, GeminiService)
 * - Database (KelazZzDatabase)
 * - Preferences (DataStore, UserPreferences)
 */
val dataModule = module {
    // ==================== NETWORK ====================
    single { HttpClientFactory.create(enableLogging = true) }
    singleOf(::GeminiService)
    
    // ==================== DATABASE ====================
    single {
        val driverFactory: DatabaseDriverFactory = get()
        KelazZzDatabase(driverFactory.createDriver())
    }
    
    // ==================== PREFERENCES ====================
    single { get<DataStoreFactory>().create() }
    single { UserPreferences(get()) }
}
