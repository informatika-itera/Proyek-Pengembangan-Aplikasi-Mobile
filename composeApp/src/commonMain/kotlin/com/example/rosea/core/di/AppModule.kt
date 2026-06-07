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
import com.example.rosea.domain.repository.ProductRepository
import com.example.rosea.data.repository.ProductRepositoryImpl
import com.example.rosea.domain.repository.CartRepository
import com.example.rosea.data.repository.CartRepositoryImpl
import com.example.rosea.domain.repository.OrderRepository
import com.example.rosea.data.repository.OrderRepositoryImpl
import com.example.rosea.domain.usecase.OrderSyncManager
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
    single<ProductRepository> { ProductRepositoryImpl(get(), get()) }
    single<CartRepository> { CartRepositoryImpl(get()) }
    singleOf(::AIRepositoryImpl) bind AIRepository::class
    single<OrderRepository> { OrderRepositoryImpl(get()) }
}

val useCaseModule = module {
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
