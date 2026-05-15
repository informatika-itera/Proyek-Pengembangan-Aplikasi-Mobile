package com.example.raillog.core.di

import com.example.raillog.core.network.HttpClientFactory
import com.example.raillog.core.util.DatabaseDriverFactory
import com.example.raillog.data.local.RailLogDatabase
import com.example.raillog.data.local.datastore.DataStoreFactory
import com.example.raillog.data.local.datastore.UserPreferences
import com.example.raillog.data.local.datastore.create
import com.example.raillog.data.remote.api.GeminiService
import com.example.raillog.data.repository.AIRepositoryImpl
import com.example.raillog.data.repository.SupplyRepositoryImpl
import com.example.raillog.domain.repository.AIRepository
import com.example.raillog.domain.repository.SupplyRepository
import com.example.raillog.presentation.screens.addsupply.AddSupplyViewModel
import com.example.raillog.presentation.screens.home.HomeViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

// ==================== NETWORK MODULE ====================

val networkModule = module {
    single { HttpClientFactory.create(enableLogging = true) }
    singleOf(::GeminiService)
}

// ==================== DATABASE MODULE ====================

val databaseModule = module {
    single {
        val driverFactory: DatabaseDriverFactory = get()
        RailLogDatabase(driverFactory.createDriver())
    }
}

// ==================== PREFERENCES MODULE ====================

val preferencesModule = module {
    single { get<DataStoreFactory>().create() }
    single { UserPreferences(get()) }
}

// ==================== REPOSITORY MODULE ====================

val repositoryModule = module {
    singleOf(::AIRepositoryImpl) bind AIRepository::class
    singleOf(::SupplyRepositoryImpl) bind SupplyRepository::class
}

// ==================== USE CASE MODULE ====================

val useCaseModule = module {
    // Use cases RailLog ditambahkan di Sprint 2
}

// ==================== VIEWMODEL MODULE ====================

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::AddSupplyViewModel)
}

// ==================== SHARED MODULES ====================

val sharedModules = listOf(
    networkModule,
    databaseModule,
    preferencesModule,
    repositoryModule,
    useCaseModule,
    viewModelModule
)

// ==================== INIT FUNCTION ====================

fun initKoin(
    platformModules: List<Module> = emptyList(),
    config: KoinAppDeclaration? = null
) {
    startKoin {
        config?.invoke(this)
        modules(platformModules + sharedModules)
    }
}
