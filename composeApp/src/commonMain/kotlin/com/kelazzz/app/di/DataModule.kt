package com.kelazzz.app.di

import com.kelazzz.app.core.network.HttpClientFactory
import com.kelazzz.app.core.util.DatabaseDriverFactory
import com.kelazzz.app.data.local.KelazZzDatabase
import com.kelazzz.app.data.local.datastore.DataStoreFactory
import com.kelazzz.app.data.local.datastore.UserPreferences
import com.kelazzz.app.data.local.datastore.create
import com.kelazzz.app.data.remote.gemini.GeminiService
import com.kelazzz.app.data.remote.pocket.PocketApiService
import com.kelazzz.app.data.repository.AuthRepositoryImpl
import com.kelazzz.app.data.repository.JadwalRepositoryImpl
import com.kelazzz.app.domain.repository.AuthRepository
import com.kelazzz.app.domain.repository.JadwalRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Data Layer — Koin DI Module
 * 
 * Menyediakan dependencies untuk data layer:
 * - Network (HttpClient, PocketApiService, GeminiService)
 * - Database (KelazZzDatabase)
 * - Preferences (DataStore, UserPreferences)
 * - Repositories
 */
val dataModule = module {
    // ==================== NETWORK ====================
    single { HttpClientFactory.create(enableLogging = true) }
    singleOf(::PocketApiService)
    singleOf(::GeminiService)
    
    // ==================== DATABASE ====================
    single {
        val driverFactory: DatabaseDriverFactory = get()
        KelazZzDatabase(driverFactory.createDriver())
    }
    
    // ==================== PREFERENCES ====================
    single { get<DataStoreFactory>().create() }
    single { UserPreferences(get()) }
    
    // ==================== REPOSITORIES ====================
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<JadwalRepository> { JadwalRepositoryImpl(get()) }
}
