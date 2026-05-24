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
import com.example.gamenews.presentation.screens.detail.GameDetailViewModel
import com.example.gamenews.presentation.screens.wishlist.WishlistViewModel
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

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

val databaseModule = module {
    single {
        val driverFactory: DatabaseDriverFactory = get()
        GameDatabase(driverFactory.createDriver())
    }
}

val preferencesModule = module {
    single { get<DataStoreFactory>().create() }
    single { UserPreferences(get()) }
}

val repositoryModule = module {
    single<GameRepository> { GameRepositoryImpl(get(), get()) }
    singleOf(::AIRepositoryImpl) bind AIRepository::class
}

val useCaseModule = module {
}

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::AIAssistantViewModel)
    viewModelOf(::GameDetailViewModel)
    viewModelOf(::WishlistViewModel)
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