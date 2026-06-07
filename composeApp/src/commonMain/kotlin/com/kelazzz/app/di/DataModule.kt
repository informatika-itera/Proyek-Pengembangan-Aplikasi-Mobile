package com.kelazzz.app.di

import com.kelazzz.app.core.network.HttpClientFactory
import com.kelazzz.app.core.util.DatabaseDriverFactory
import com.kelazzz.app.data.local.KelazZzDatabase
import com.kelazzz.app.data.local.datastore.DataStoreFactory
import com.kelazzz.app.data.local.datastore.UserPreferences
import com.kelazzz.app.data.local.datastore.create
import com.kelazzz.app.data.remote.ai.ChatToolHandler
import com.kelazzz.app.data.remote.ai.OpenCodeGoService
import com.kelazzz.app.data.remote.pocket.PocketApiService
import com.kelazzz.app.data.repository.AIRepositoryImpl
import com.kelazzz.app.data.repository.AuthRepositoryImpl
import com.kelazzz.app.data.repository.JadwalRepositoryImpl
import com.kelazzz.app.data.repository.PresensiRepositoryImpl
import com.kelazzz.app.domain.repository.AIRepository
import com.kelazzz.app.domain.repository.AuthRepository
import com.kelazzz.app.domain.repository.JadwalRepository
import com.kelazzz.app.domain.repository.PresensiRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Data Layer — Koin DI Module
 * 
 * Menyediakan dependencies untuk data layer:
 * - Network (HttpClient, PocketApiService, OpenCodeGoService)
 * - Database (KelazZzDatabase)
 * - Preferences (DataStore, UserPreferences)
 * - Repositories
 * - AI Tool Handler
 */
val dataModule = module {
    // ==================== NETWORK ====================
    // Prompt AI dapat berisi data akademik pengguna, jadi jangan log body HTTP secara default.
    single { HttpClientFactory.create(enableLogging = false) }
    singleOf(::PocketApiService)
    singleOf(::OpenCodeGoService)
    
    // ==================== DATABASE ====================
    single {
        val driverFactory: DatabaseDriverFactory = get()
        KelazZzDatabase(driverFactory.createDriver())
    }
    
    // ==================== PREFERENCES ====================
    single { get<DataStoreFactory>().create() }
    single { UserPreferences(get()) }
    
    // ==================== AI TOOL HANDLER ====================
    single { ChatToolHandler(get(), get(), get()) }
    
    // ==================== REPOSITORIES ====================
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<JadwalRepository> { JadwalRepositoryImpl(get(), get()) }
    single<PresensiRepository> { PresensiRepositoryImpl(get(), get(), get()) }
    single<AIRepository> { AIRepositoryImpl(get(), get()) }
}
