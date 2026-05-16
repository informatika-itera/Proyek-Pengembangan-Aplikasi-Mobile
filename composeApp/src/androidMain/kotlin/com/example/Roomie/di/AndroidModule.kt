package com.example.Roomie.di

import com.example.Roomie.core.network.AndroidNetworkMonitor
import com.example.Roomie.core.network.NetworkMonitor
import com.example.Roomie.core.util.DatabaseDriverFactory
import com.example.Roomie.data.local.datastore.DataStoreFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    single { DatabaseDriverFactory(androidContext()) }
    single { DataStoreFactory(androidContext()) }
    single<NetworkMonitor> { AndroidNetworkMonitor(androidContext()) }
}
