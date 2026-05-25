package com.example.mybawanggacha.core.di

import com.example.mybawanggacha.core.util.DatabaseDriverFactory
import com.example.mybawanggacha.data.local.datastore.DataStoreFactory
import org.koin.dsl.module

val iosModule = module {
    single { DatabaseDriverFactory() }
    single { DataStoreFactory() }
}

private var koinStarted = false

fun initKoinIOS() {
    if (koinStarted) return

    initKoin(
        platformModules = listOf(iosModule)
    )

    koinStarted = true
}