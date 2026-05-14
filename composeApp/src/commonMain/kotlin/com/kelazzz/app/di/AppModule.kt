package com.kelazzz.app.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

/**
 * App Module — Root Koin DI Configuration
 * 
 * Mengumpulkan semua sub-modules:
 * - dataModule: Network, Database, Preferences
 * - viewModelModule: ViewModels
 * 
 * Modular DI setup mengikuti Clean Architecture:
 * setiap layer memiliki module sendiri.
 */
val sharedModules = listOf(
    dataModule,
    viewModelModule
)

/**
 * Initialize Koin DI
 * 
 * Dipanggil dari platform-specific entry point:
 * - Android: KelazZzApplication.onCreate()
 * - iOS: MainViewController (jika diimplementasi)
 * 
 * @param platformModules Module platform-specific (AndroidModule, dsb.)
 * @param config Konfigurasi tambahan (androidContext, logger, dsb.)
 */
fun initKoin(
    platformModules: List<Module> = emptyList(),
    config: KoinAppDeclaration? = null
) {
    startKoin {
        config?.invoke(this)
        modules(platformModules + sharedModules)
    }
}
