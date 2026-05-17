package com.example.musickeep.core.di

import com.example.musickeep.core.network.HttpClientFactory
import com.example.musickeep.core.util.DatabaseDriverFactory
import com.example.musickeep.data.local.MusicDatabase
import com.example.musickeep.data.local.datastore.DataStoreFactory
import com.example.musickeep.data.local.datastore.UserPreferences
import com.example.musickeep.presentation.screens.addmusic.AddMusicViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
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

// ==================== VIEWMODEL MODULE ====================

val viewModelModule = module {
    viewModelOf(::AddMusicViewModel)
}

// ==================== SHARED MODULES ====================

val sharedModules = listOf(
    networkModule,
    databaseModule,
    preferencesModule,
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
