package com.example.musickeep.core.di

import com.example.musickeep.core.network.HttpClientFactory
import com.example.musickeep.core.util.DatabaseDriverFactory
import com.example.musickeep.data.local.MusicDatabase
import com.example.musickeep.data.local.datastore.DataStoreFactory
import com.example.musickeep.data.local.datastore.UserPreferences
import com.example.musickeep.data.repository.MusicRepositoryImpl
import com.example.musickeep.domain.repository.MusicRepository
import com.example.musickeep.presentation.screens.addmusic.AddMusicViewModel
import com.example.musickeep.presentation.screens.detail.MusicDetailViewModel
import com.example.musickeep.presentation.screens.home.HomeViewModel
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
}

// ==================== DATABASE MODULE ====================

val databaseModule = module {
    single {
        val driverFactory: DatabaseDriverFactory = get()
        MusicDatabase(driverFactory.createDriver())
    }
}

// ==================== PREFERENCES MODULE ====================

val preferencesModule = module {
    single { get<DataStoreFactory>().create() }
    single { UserPreferences(get()) }
}

// ==================== REPOSITORY MODULE ====================

val repositoryModule = module {
    singleOf(::MusicRepositoryImpl) bind MusicRepository::class
}

// ==================== VIEWMODEL MODULE ====================

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::AddMusicViewModel)
    viewModelOf(::MusicDetailViewModel)
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
