package com.example.neurodeck.core.di

import com.example.neurodeck.core.network.HttpClientFactory
import com.example.neurodeck.core.util.DatabaseDriverFactory
import com.example.neurodeck.data.local.NeuroDeckDatabase
import com.example.neurodeck.domain.usecase.CalculateNextReviewUseCase
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

// ==================== NETWORK MODULE ====================

val networkModule = module {
    single { HttpClientFactory.create(enableLogging = true) }
}

// ==================== DATABASE MODULE ====================

val databaseModule = module {
    single { NeuroDeckDatabase(get<DatabaseDriverFactory>().createDriver()) }
}

// ==================== REPOSITORY MODULE ====================
// TODO Fase D: register DeckRepository, CardRepository di sini

val repositoryModule = module {
    // empty for now
}

// ==================== USE CASE MODULE ====================

val useCaseModule = module {
    single { CalculateNextReviewUseCase() }
    // TODO Fase D+: tambah use case lain di sini (jika perlu wrapping)
}

// ==================== VIEWMODEL MODULE ====================
// TODO Fase D: register ViewModels di sini

val viewModelModule = module {
    // empty for now
}

// ==================== SHARED MODULES ====================

val sharedModules = listOf(
    networkModule,
    databaseModule,
    repositoryModule,
    useCaseModule,
    viewModelModule,
)

// ==================== INIT FUNCTION ====================

fun initKoin(
    platformModules: List<Module> = emptyList(),
    config: KoinAppDeclaration? = null,
) {
    startKoin {
        config?.invoke(this)
        modules(platformModules + sharedModules)
    }
}