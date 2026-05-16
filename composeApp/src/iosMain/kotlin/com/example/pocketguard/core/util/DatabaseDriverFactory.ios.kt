package com.example.pocketguard.core.util

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.pocketguard.data.local.PocketGuardDatabase

/**
 * iOS implementation of DatabaseDriverFactory
 * 
 * Menggunakan NativeSqliteDriver yang membungkus SQLite native iOS.
 * Database disimpan di Documents directory aplikasi.
 */
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = PocketGuardDatabase.Schema,
            name = "PocketGuard.db"
        )
    }
}
