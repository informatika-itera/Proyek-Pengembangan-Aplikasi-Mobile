package com.example.foodsaver.core.util

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.foodsaver.data.local.FoodSaverDatabase

/**
 * Implementasi iOS untuk DatabaseDriverFactory.
 */
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = FoodSaverDatabase.Schema,
            name = "foodsaver.db"
        )
    }
}
