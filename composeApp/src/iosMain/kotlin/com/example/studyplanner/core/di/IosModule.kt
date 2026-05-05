package com.example.studyplanner.core.di

import com.example.studyplanner.core.util.DatabaseDriverFactory
import com.example.studyplanner.data.local.datastore.DataStoreFactory
import org.koin.dsl.module

/**
 * iOS-specific Koin module.
 *
 * Menyediakan dependencies platform yang dipakai di shared modules.
 */
val iosModule = module {
    single { DatabaseDriverFactory() }
    single { DataStoreFactory() }
}

/** Helper untuk dipanggil dari Swift code. */
fun initKoinIOS() {
    initKoin(platformModules = listOf(iosModule))
}
