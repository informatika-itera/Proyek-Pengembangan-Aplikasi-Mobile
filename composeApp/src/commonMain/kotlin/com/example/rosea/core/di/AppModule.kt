package com.example.rosea.core.di

import com.example.rosea.core.network.HttpClientFactory
import com.example.rosea.core.util.DatabaseDriverFactory
import com.example.rosea.data.local.NoteDatabase
import com.example.rosea.data.local.datastore.DataStoreFactory
import com.example.rosea.data.local.datastore.UserPreferences
import com.example.rosea.data.local.datastore.create
import com.example.rosea.data.remote.api.GeminiService
import com.example.rosea.data.repository.AIRepositoryImpl
import com.example.rosea.domain.repository.AIRepository
import com.example.rosea.data.remote.api.ProductApiService

// IMPORT REPOSITORY LAMA
import com.example.rosea.domain.repository.ProductRepository
import com.example.rosea.data.repository.ProductRepositoryImpl
import com.example.rosea.domain.repository.CartRepository
import com.example.rosea.data.repository.CartRepositoryImpl

// === 🌟 IMPORT KELAS BARU UNTUK FITUR OFFLINE CHECKOUT ===
import com.example.rosea.domain.repository.OrderRepository
import com.example.rosea.data.repository.OrderRepositoryImpl
import com.example.rosea.domain.usecase.OrderSyncManager

// IMPORT VIEWMODEL
import com.example.rosea.presentation.screens.ai.AIAssistantViewModel
import com.example.rosea.presentation.screens.home.HomeViewModel
import com.example.rosea.presentation.screens.detail.DetailViewModel
import com.example.rosea.presentation.screens.cart.CartViewModel

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

val networkModule = module {
    single { HttpClientFactory.create(enableLogging = true) }
    singleOf(::GeminiService)
    singleOf(::ProductApiService)
}

val databaseModule = module {
    single {
        val driverFactory: DatabaseDriverFactory = get()
        NoteDatabase(driverFactory.createDriver())
    }
}

val preferencesModule = module {
    single { get<DataStoreFactory>().create() }
    single { UserPreferences(get()) }
}

val repositoryModule = module {
    singleOf(::ProductRepositoryImpl) bind ProductRepository::class
    singleOf(::CartRepositoryImpl) bind CartRepository::class
    singleOf(::AIRepositoryImpl) bind AIRepository::class
    // Daftarkan OrderRepository
    singleOf(::OrderRepositoryImpl) bind OrderRepository::class
}

val useCaseModule = module {
    // Daftarkan Mesin Sinkronisasi
    singleOf(::OrderSyncManager)
}

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::AIAssistantViewModel)
    viewModelOf(::DetailViewModel)
    viewModelOf(::CartViewModel)
}

val sharedModules = listOf(
    networkModule,
    databaseModule,
    preferencesModule,
    repositoryModule,
    useCaseModule,
    viewModelModule
)

fun initKoin(
    platformModules: List<Module> = emptyList(),
    config: KoinAppDeclaration? = null
) {
    startKoin {
        config?.invoke(this)
        modules(platformModules + sharedModules)
    }
}