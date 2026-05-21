package com.example.foodsaver.core.di

import app.cash.sqldelight.db.SqlDriver
import com.example.foodsaver.core.util.DatabaseDriverFactory
import com.example.foodsaver.data.local.datastore.DataStoreFactory
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Modul Koin khusus untuk platform iOS.
 */
val iosModule = module {
    single<SqlDriver> { DatabaseDriverFactory().createDriver() }
    single { DataStoreFactory() }
}

/**
 * Fungsi inisialisasi Koin yang akan dipanggil dari Swift (iOS).
 */
fun initKoinIOS() = initKoin(
    platformModules = listOf(iosModule)
)
