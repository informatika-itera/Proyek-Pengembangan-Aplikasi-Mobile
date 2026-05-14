package com.example.foodsaver.core.util

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
// import com.example.foodsaver.db.FoodSaverDatabase

/**
 * Implementasi iOS untuk DatabaseDriverFactory.
 */
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = NativeSqliteDriver.Schema, // Ganti ke FoodSaverDatabase.Schema nanti
            name = "foodsaver.db"
        )
    }
}
