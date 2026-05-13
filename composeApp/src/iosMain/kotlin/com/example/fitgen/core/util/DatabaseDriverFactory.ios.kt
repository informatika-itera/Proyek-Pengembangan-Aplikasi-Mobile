package com.example.fitgen.core.util

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.fitgen.data.local.FitGenDatabase

/**
 * iOS implementation of DatabaseDriverFactory
 * 
 * Menggunakan NativeSqliteDriver yang membungkus SQLite native iOS.
 * Database disimpan di Documents directory aplikasi.
 */
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = FitGenDatabase.Schema,
            name = "fitgen.db"
        )
    }
}
