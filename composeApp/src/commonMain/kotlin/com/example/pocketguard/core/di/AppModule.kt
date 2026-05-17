package com.example.pocketguard.core.di

import com.example.pocketguard.core.network.HttpClientFactory
import com.example.pocketguard.core.util.DatabaseDriverFactory
import com.example.pocketguard.data.local.PocketGuardDatabase
import com.example.pocketguard.data.local.datastore.DataStoreFactory
import com.example.pocketguard.data.local.datastore.UserPreferences
import com.example.pocketguard.data.local.datastore.create
import com.example.pocketguard.data.remote.api.GeminiService
import com.example.pocketguard.data.repository.AIRepositoryImpl
import com.example.pocketguard.data.repository.TransactionRepositoryImpl
import com.example.pocketguard.domain.repository.AIRepository
import com.example.pocketguard.domain.repository.TransactionRepository
import com.example.pocketguard.domain.usecase.* // Import Use Cases
import com.example.pocketguard.presentation.screens.add_transaction.AddTransactionViewModel
import com.example.pocketguard.presentation.screens.ai.AIAssistantViewModel
import com.example.pocketguard.presentation.screens.detail.TransactionDetailViewModel
import com.example.pocketguard.presentation.screens.home.HomeViewModel
import com.example.pocketguard.presentation.screens.settings.SettingsViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

// ==================== NETWORK & DB MODULE ====================
val networkModule = module {
    single { HttpClientFactory.create(enableLogging = true) }
    singleOf(::GeminiService)
}

val databaseModule = module {
    single {
        val driverFactory: DatabaseDriverFactory = get()
        PocketGuardDatabase(driverFactory.createDriver())
    }
}

val preferencesModule = module {
    single { get<DataStoreFactory>().create() }
    single { UserPreferences(get()) }
}

// ==================== REPOSITORY MODULE ====================
val repositoryModule = module {
    singleOf(::TransactionRepositoryImpl) bind TransactionRepository::class
    singleOf(::AIRepositoryImpl) bind AIRepository::class
}

// ==================== USE CASE MODULE (Daftarkan di Sini) ====================
val useCaseModule = module {
    singleOf(::GetAllTransactionsUseCase)
    singleOf(::SaveTransactionUseCase)
    singleOf(::DeleteTransactionUseCase)
    singleOf(::AnalyzeFinanceUseCase)
}

// ==================== VIEWMODEL MODULE ====================
val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::AIAssistantViewModel)
    viewModelOf(::AddTransactionViewModel)    // Tambahkan ini
    viewModelOf(::TransactionDetailViewModel) // Tambahkan ini
    viewModelOf(::SettingsViewModel)
}

val sharedModules = listOf(
    networkModule,
    databaseModule,
    preferencesModule,
    repositoryModule,
    useCaseModule,
    viewModelModule
)

fun initKoin(platformModules: List<Module> = emptyList(), config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(platformModules + sharedModules)
    }
}