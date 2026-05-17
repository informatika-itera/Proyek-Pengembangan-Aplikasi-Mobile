package com.example.tabungin.core.di

import com.example.tabungin.core.util.DatabaseDriverFactory
import com.example.tabungin.data.local.datastore.DataStoreFactory
import org.koin.dsl.module

val iosModule = module {
    single { DatabaseDriverFactory() }
    single { DataStoreFactory() }
}


fun initKoinIOS() {
    initKoin(platformModules = listOf(iosModule))
}
