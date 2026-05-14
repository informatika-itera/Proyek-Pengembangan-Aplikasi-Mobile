package com.example.Roomie.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

/**
 * Inisialisasi Koin DI
 * Menggabungkan semua module (Data, Domain, ViewModel)
 */
fun initKoin(
    platformModules: List<Module> = emptyList(),
    config: KoinAppDeclaration? = null
) {
    startKoin {
        config?.invoke(this)
        modules(
            platformModules + 
            dataModule + 
            domainModule + 
            viewModelModule
        )
    }
}
