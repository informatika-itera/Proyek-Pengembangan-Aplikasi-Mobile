package com.example.mybawanggacha.core.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val sharedModules = listOf(
    dispatchersModule,
    networkModule,
    databaseModule,
    preferencesModule,
    repositoryModule,
    useCaseModule,
    viewModelModule
)

fun initKoin(
    platformModules: List<Module> = emptyList(),
    enableNetworkLogging: Boolean = false,
    config: KoinAppDeclaration? = null
) {
    val runtimeModule = module {
        single(named(NETWORK_LOGGING_ENABLED_QUALIFIER)) { enableNetworkLogging }
    }
    startKoin {
        config?.invoke(this)
        modules(platformModules + runtimeModule + sharedModules)
    }
}
