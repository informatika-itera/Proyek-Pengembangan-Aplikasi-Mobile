package com.soundletter.app.core.di

import com.soundletter.app.core.network.HttpClientFactory
import com.soundletter.app.core.util.DatabaseDriverFactory
import com.soundletter.app.data.local.SoundLetterDatabase
import com.soundletter.app.presentation.screens.detail.DetailViewModel
import com.soundletter.app.presentation.screens.home.HomeViewModel
import com.soundletter.app.presentation.screens.search.SearchViewModel
import com.soundletter.app.presentation.screens.compose.ComposeViewModel
import com.soundletter.app.presentation.screens.history.HistoryViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
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
        SoundLetterDatabase(driverFactory.createDriver())
    }
}

// ==================== VIEWMODEL MODULE ====================

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::ComposeViewModel)
    viewModelOf(::HistoryViewModel)
    viewModelOf(::DetailViewModel)
}

// ==================== SHARED MODULES ====================

val sharedModules = listOf(
    networkModule,
    databaseModule,
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
