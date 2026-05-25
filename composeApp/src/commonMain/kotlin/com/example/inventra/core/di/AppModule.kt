package com.example.inventra.core.di

import com.example.inventra.core.network.HttpClientFactory
import com.example.inventra.core.util.DatabaseDriverFactory
import com.example.inventra.data.local.InventRaDatabase
import com.example.inventra.data.local.datastore.DataStoreFactory
import com.example.inventra.data.local.datastore.UserPreferences
import com.example.inventra.data.local.datastore.create
import com.example.inventra.data.remote.api.GeminiService
import com.example.inventra.data.repository.AIRepositoryImpl
import com.example.inventra.data.repository.BorrowRepositoryImpl
import com.example.inventra.data.repository.ItemRepositoryImpl
import com.example.inventra.domain.repository.AIRepository
import com.example.inventra.domain.repository.BorrowRepository
import com.example.inventra.domain.repository.ItemRepository
import com.example.inventra.domain.usecase.*
import com.example.inventra.presentation.screens.addedit.AddEditItemViewModel
import com.example.inventra.presentation.screens.ai.AIInventoryViewModel
import com.example.inventra.presentation.screens.catalog.CatalogViewModel
import com.example.inventra.presentation.screens.dashboard.DashboardViewModel
import com.example.inventra.presentation.screens.detail.ItemDetailViewModel
import com.example.inventra.presentation.screens.history.HistoryViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
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
        InventRaDatabase(driverFactory.createDriver())
    }
}

// ==================== PREFERENCES MODULE ====================

val preferencesModule = module {
    single { get<DataStoreFactory>().create() }
    single { UserPreferences(get()) }
}

// ==================== REPOSITORY MODULE ====================

val repositoryModule = module {
    singleOf(::ItemRepositoryImpl) bind ItemRepository::class
    singleOf(::BorrowRepositoryImpl) bind BorrowRepository::class
    singleOf(::AIRepositoryImpl) bind AIRepository::class
}

// ==================== USE CASE MODULE ====================

val useCaseModule = module {
    singleOf(::GetAllItemsUseCase)
    singleOf(::SearchItemsUseCase)
    singleOf(::SaveItemUseCase)
    singleOf(::DeleteItemUseCase)
}

// ==================== VIEWMODEL MODULE ====================

val viewModelModule = module {
    viewModelOf(::DashboardViewModel)
    viewModelOf(::CatalogViewModel)
    viewModelOf(::HistoryViewModel)
    viewModelOf(::AIInventoryViewModel)
    viewModel { (itemId: Long) -> ItemDetailViewModel(itemId, get(), get()) }
    viewModel { (itemId: Long?) -> AddEditItemViewModel(itemId, get(), get()) }
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
