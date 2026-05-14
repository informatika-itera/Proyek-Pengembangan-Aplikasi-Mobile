package com.example.foodsaver.core.di

import com.example.foodsaver.core.network.HttpClientFactory
import com.example.foodsaver.data.remote.api.GeminiService
import com.example.foodsaver.data.repository.AIRepositoryImpl
import com.example.foodsaver.domain.repository.AIRepository
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/**
 * Common Koin module for FoodSaver.
 */
val commonModule = module {
    // Network
    single { HttpClientFactory().create() }
    single { GeminiService(get()) }

    // Repositories
    single<AIRepository> { AIRepositoryImpl(get()) }
    
    // Use Cases (akan ditambahkan di sprint berikutnya)
}

/**
 * Initialize Koin for all platforms.
 */
fun initKoin(
    platformModules: List<Module> = emptyList(),
    config: KoinAppDeclaration? = null
) {
    startKoin {
        config?.invoke(this)
        modules(commonModule + platformModules)
    }
}
