package com.example.foodsaver.core.injection

import app.cash.sqldelight.db.SqlDriver
import com.example.foodsaver.core.utility.DatabaseDriverFactory
import com.example.foodsaver.data.local.datastore.DataStoreFactory
import org.koin.core.module.Module
import org.koin.dsl.module

val iosModule = module {
    single<SqlDriver> { DatabaseDriverFactory().createDriver() }
    single { DataStoreFactory() }
}

fun initKoinIOS() = initKoin(
    platformModules = listOf(iosModule)
)
