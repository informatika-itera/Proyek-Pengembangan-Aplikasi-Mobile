package com.example.metaforge.core.di

import com.example.metaforge.core.network.HttpClientFactory
import com.example.metaforge.core.util.DatabaseDriverFactory
import com.example.metaforge.data.local.MetaForgeDatabase
import com.example.metaforge.data.local.datastore.DataStoreFactory
import com.example.metaforge.data.local.datastore.DraftPreferences
import com.example.metaforge.data.local.datastore.UserPreferences
import com.example.metaforge.data.local.datastore.create
import com.example.metaforge.data.remote.api.GeminiService
import com.example.metaforge.data.repository.AIRepositoryImpl
import com.example.metaforge.data.repository.DraftRepositoryImpl
import com.example.metaforge.domain.repository.AIRepository
import com.example.metaforge.domain.repository.DraftRepository
import com.example.metaforge.presentation.screens.counterpick.CounterPickViewModel
import com.example.metaforge.presentation.screens.draft.DraftViewModel
import com.example.metaforge.presentation.screens.heroselect.HeroSelectViewModel
import com.example.metaforge.presentation.screens.synergy.SynergyViewModel
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
        MetaForgeDatabase(driverFactory.createDriver())
    }
}

// ==================== PREFERENCES MODULE ====================
val preferencesModule = module {
    single { get<DataStoreFactory>().create() }
    single { UserPreferences(get()) }
    single { DraftPreferences(get()) }
}

// ==================== REPOSITORY MODULE ====================
val repositoryModule = module {
    singleOf(::AIRepositoryImpl) bind AIRepository::class
    single<DraftRepository> { DraftRepositoryImpl(get()) }
}

// ==================== VIEWMODEL MODULE ====================
val viewModelModule = module {
    viewModelOf(::DraftViewModel)
    viewModelOf(::HeroSelectViewModel)
    viewModelOf(::SynergyViewModel)
    viewModelOf(::CounterPickViewModel)
}

// ==================== SHARED MODULES ====================
val sharedModules = listOf(
    networkModule,
    databaseModule,
    preferencesModule,
    repositoryModule,
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