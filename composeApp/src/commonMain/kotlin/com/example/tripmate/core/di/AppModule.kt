package com.example.tripmate.core.di

import com.example.tripmate.core.network.HttpClientFactory
import com.example.tripmate.data.local.TripDatabase
import com.example.tripmate.data.remote.api.GeminiService
import com.example.tripmate.data.repository.AIRepositoryImpl
import com.example.tripmate.data.repository.TripRepositoryImpl
import com.example.tripmate.domain.repository.TripRepository
import com.example.tripmate.presentation.screens.ai.AIViewModel
import com.example.tripmate.presentation.screens.home.TripViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val sharedModules = module {
    single { TripDatabase(get<com.example.tripmate.core.util.DatabaseDriverFactory>().createDriver()) }
    single { HttpClientFactory.create() }
    single { GeminiService(get()) }
    single<TripRepository> { TripRepositoryImpl(get()) }
    single { AIRepositoryImpl(get()) }
}

val viewModelModule = module {
    viewModelOf(::TripViewModel)
    viewModelOf(::AIViewModel)
}

fun initKoin(
    platformModules: List<Module> = emptyList(),
    appDeclaration: KoinAppDeclaration = {}
) {
    startKoin {
        appDeclaration()
        modules(platformModules + sharedModules + viewModelModule)
    }
}
