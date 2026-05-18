package com.example.Roomie.di

import com.example.Roomie.core.network.IosNetworkMonitor
import com.example.Roomie.core.network.NetworkMonitor
import com.example.Roomie.core.util.DatabaseDriverFactory
import com.example.Roomie.data.local.datastore.DataStoreFactory
import org.koin.dsl.module

val iosModule = module {
    single { DatabaseDriverFactory() }
    single { DataStoreFactory() }
    single<NetworkMonitor> { IosNetworkMonitor() }
}

fun initKoinIOS() {
    initKoin(platformModules = listOf(iosModule))
}
