package com.example.inventra.core.di

import com.example.inventra.core.network.HttpClientFactory
import com.example.inventra.core.util.DatabaseDriverFactory
import com.example.inventra.data.local.InventRaDatabase
import com.example.inventra.data.local.datastore.DataStoreFactory
import com.example.inventra.data.local.datastore.UserPreferences
import com.example.inventra.data.local.datastore.UserPreferencesImpl
import com.example.inventra.data.local.datastore.create
import com.example.inventra.data.remote.api.GeminiService
import com.example.inventra.data.repository.AIRepositoryImpl
import com.example.inventra.data.repository.AuthRepositoryImpl
import com.example.inventra.data.repository.BorrowRepositoryImpl
import com.example.inventra.data.repository.ItemRepositoryImpl
import com.example.inventra.domain.repository.AIRepository
import com.example.inventra.domain.repository.AuthRepository
import com.example.inventra.domain.repository.BorrowRepository
import com.example.inventra.domain.repository.ItemRepository
import com.example.inventra.domain.usecase.DeleteItemUseCase
import com.example.inventra.domain.usecase.GetAllItemsUseCase
import com.example.inventra.domain.usecase.SaveItemUseCase
import com.example.inventra.domain.usecase.SearchItemsUseCase
import com.example.inventra.presentation.screens.addedit.AddEditItemViewModel
import com.example.inventra.presentation.screens.ai.AIInventoryViewModel
import com.example.inventra.presentation.screens.auth.LoginViewModel
import com.example.inventra.presentation.screens.catalog.CatalogViewModel
import com.example.inventra.presentation.screens.dashboard.DashboardViewModel
import com.example.inventra.presentation.screens.detail.ItemDetailViewModel
import com.example.inventra.presentation.screens.history.HistoryViewModel
import com.example.inventra.presentation.screens.management.UserDetailViewModel
import com.example.inventra.presentation.screens.management.UserManagementViewModel
import com.example.inventra.presentation.screens.profile.ProfileViewModel
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
}

val databaseModule = module {
    single {
        val driverFactory: DatabaseDriverFactory = get()
        InventRaDatabase(driverFactory.createDriver())
    }
}

val preferencesModule = module {
    single { get<DataStoreFactory>().create() }
    single<UserPreferences> { UserPreferencesImpl(get()) }
}

val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<ItemRepository> { ItemRepositoryImpl(get()) }
    single<BorrowRepository> { BorrowRepositoryImpl(get()) }
    singleOf(::AIRepositoryImpl) bind AIRepository::class
}

val useCaseModule = module {
    singleOf(::GetAllItemsUseCase)
    singleOf(::SearchItemsUseCase)
    singleOf(::SaveItemUseCase)
    singleOf(::DeleteItemUseCase)
}

val viewModelModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::DashboardViewModel)
    viewModelOf(::CatalogViewModel)
    viewModelOf(::HistoryViewModel)
    viewModelOf(::AIInventoryViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::UserManagementViewModel)
    viewModelOf(::UserDetailViewModel)
    factory { (itemId: Long?) -> AddEditItemViewModel(itemId, get(), get()) }
    factory { (itemId: Long) -> ItemDetailViewModel(itemId, get(), get(), get()) }
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