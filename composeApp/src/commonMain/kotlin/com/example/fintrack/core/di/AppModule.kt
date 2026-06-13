package com.example.fintrack.core.di

import com.example.fintrack.core.network.HttpClientFactory
import com.example.fintrack.core.util.DatabaseDriverFactory
import com.example.fintrack.data.local.FinTrackDatabase
import com.example.fintrack.data.local.datastore.DataStoreFactory
import com.example.fintrack.data.local.datastore.UserPreferences
import com.example.fintrack.data.local.datastore.create
import com.example.fintrack.data.remote.api.ExchangeApiService
import com.example.fintrack.data.remote.api.GeminiService
import com.example.fintrack.data.repository.AIRepositoryImpl
import com.example.fintrack.data.repository.TransactionRepositoryImpl
import com.example.fintrack.domain.repository.AIRepository
import com.example.fintrack.domain.repository.TransactionRepository
import com.example.fintrack.presentation.screens.add_edit.AddEditViewModel
import com.example.fintrack.presentation.screens.home.HomeViewModel
import com.example.fintrack.presentation.screens.history.HistoryViewModel
import com.example.fintrack.presentation.screens.detail.DetailViewModel
import com.example.fintrack.presentation.screens.exchange.ExchangeViewModel
import com.example.fintrack.presentation.screens.settings.SettingsViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

// ==================== NETWORK MODULE ====================


val networkModule = module {
    single { HttpClientFactory.create(enableLogging = true) }
    single { GeminiService(get()) }
    single { ExchangeApiService(get()) }
}

// ==================== DATABASE MODULE ====================

val databaseModule = module {
    single {
        val driverFactory: DatabaseDriverFactory = get()
        FinTrackDatabase(driverFactory.createDriver())
    }
}

// ==================== PREFERENCES MODULE ====================

val preferencesModule = module {
    single { get<DataStoreFactory>().create() }
    single { UserPreferences(get()) }
}

// ==================== REPOSITORY MODULE ====================

val repositoryModule = module {
    singleOf(::TransactionRepositoryImpl) bind TransactionRepository::class
    singleOf(::AIRepositoryImpl) bind AIRepository::class
}

// ==================== USE CASE MODULE ====================

val useCaseModule = module {
    // Transaction use cases will go here if needed, or viewmodels can use repository directly
}

// ==================== VIEWMODEL MODULE ====================

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::AddEditViewModel)
    viewModelOf(::HistoryViewModel)
    viewModelOf(::DetailViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::ExchangeViewModel)
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
