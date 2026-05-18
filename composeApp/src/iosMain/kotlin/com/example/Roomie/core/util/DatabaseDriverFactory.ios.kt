package com.example.Roomie.core.util

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.Roomie.core.util.AppConfig
import com.example.Roomie.data.local.RoomieDatabase

/**
 * iOS implementation of DatabaseDriverFactory
 * 
 * Menggunakan NativeSqliteDriver yang membungkus SQLite native iOS.
 * Database disimpan di Documents directory aplikasi.
 */
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = RoomieDatabase.Schema,
            name = AppConfig.databaseName
        )
    }
}
