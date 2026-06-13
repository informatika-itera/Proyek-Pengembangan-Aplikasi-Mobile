package com.example.foodsaver.core.utility

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.foodsaver.data.local.FoodSaverDatabase

/**
 * Implementasi iOS untuk DatabaseDriverFactory.
 * Nama database diubah untuk sinkronisasi skema terbaru.
 */
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = FoodSaverDatabase.Schema,
            name = "foodsaver_v2.db"
        )
    }
}
