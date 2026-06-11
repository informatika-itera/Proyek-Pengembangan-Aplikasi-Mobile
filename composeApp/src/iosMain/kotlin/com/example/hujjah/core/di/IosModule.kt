package com.example.hujjah.core.di

import com.example.hujjah.core.network.IosNetworkMonitor
import com.example.hujjah.core.network.NetworkMonitor
import com.example.hujjah.core.util.DatabaseDriverFactory
import com.example.hujjah.data.local.datastore.DataStoreFactory
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * iOS-specific Koin module.
 *
 * Menyediakan dependencies platform yang dipakai di shared modules.
 */
val iosModule = module {
    single { DatabaseDriverFactory() }
    single { DataStoreFactory() }
    single { IosNetworkMonitor() } bind NetworkMonitor::class
}

/** Helper untuk dipanggil dari Swift code. */
fun initKoinIOS() {
    initKoin(platformModules = listOf(iosModule))
}
