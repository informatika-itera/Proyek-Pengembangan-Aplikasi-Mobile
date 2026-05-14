package com.example.Roomie.di

import com.example.Roomie.core.util.DatabaseDriverFactory
import com.example.Roomie.data.local.datastore.DataStoreFactory
import org.koin.dsl.module

val iosModule = module {
    single { DatabaseDriverFactory() }
    single { DataStoreFactory() }
}

fun initKoinIOS() {
    initKoin(platformModules = listOf(iosModule))
}
