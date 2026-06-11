package com.example.hujjah.core.di

import com.example.hujjah.core.network.AndroidNetworkMonitor
import com.example.hujjah.core.network.NetworkMonitor
import com.example.hujjah.core.util.DatabaseDriverFactory
import com.example.hujjah.data.local.datastore.DataStoreFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Android-specific Koin module.
 *
 * Menyediakan dependencies yang membutuhkan `Context`:
 * - DatabaseDriverFactory: untuk SQLDelight driver
 * - DataStoreFactory     : untuk lokasi file preferences
 * - NetworkMonitor       : untuk memantau status jaringan
 */
val androidModule = module {
    single { DatabaseDriverFactory(androidContext()) }
    single { DataStoreFactory(androidContext()) }
    single { AndroidNetworkMonitor(androidContext()) } bind NetworkMonitor::class
}
