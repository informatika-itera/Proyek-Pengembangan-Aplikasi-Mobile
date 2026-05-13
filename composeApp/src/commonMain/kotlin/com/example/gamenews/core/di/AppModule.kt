package com.example.gamenews.core.di

import com.example.gamenews.GameDatabase
import com.example.gamenews.core.network.HttpClientFactory
import com.example.gamenews.core.util.DatabaseDriverFactory
import com.example.gamenews.data.local.datastore.DataStoreFactory
import com.example.gamenews.data.local.datastore.UserPreferences
import com.example.gamenews.data.local.datastore.create
import com.example.gamenews.data.remote.api.GeminiService
import com.example.gamenews.data.remote.api.GameBrainService
import com.example.gamenews.data.repository.AIRepositoryImpl
import com.example.gamenews.data.repository.GameRepositoryImpl
import com.example.gamenews.domain.repository.AIRepository
import com.example.gamenews.domain.repository.GameRepository
import com.example.gamenews.presentation.screens.ai.AIAssistantViewModel
import com.example.gamenews.presentation.screens.home.HomeViewModel
import kotlinx.serialization.json.Json // Pastikan import ini ada
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

// ==================== NETWORK MODULE ====================
val networkModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            isLenient = true
        }
    }

    single { HttpClientFactory.create(enableLogging = true) }

    singleOf(::GameBrainService)
    singleOf(::GeminiService)
}

// ==================== DATABASE MODULE ====================
val databaseModule = module {
    single {
        val driverFactory: DatabaseDriverFactory = get()
        GameDatabase(driverFactory.createDriver())
    }
}

// ==================== PREFERENCES MODULE ====================
val preferencesModule = module {
    single { get<DataStoreFactory>().create() }
    single { UserPreferences(get()) }
}

// ==================== REPOSITORY MODULE ====================
val repositoryModule = module {
    // Sekarang GameRepositoryImpl bisa dibuat karena Json dan GameBrainService sudah ada
    singleOf(::GameRepositoryImpl) bind GameRepository::class
    singleOf(::AIRepositoryImpl) bind AIRepository::class
}

// ==================== USE CASE MODULE ====================
val useCaseModule = module {
    // Kosongkan sementara atau isi jika ada
}

// ==================== VIEWMODEL MODULE ====================
val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::AIAssistantViewModel)
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