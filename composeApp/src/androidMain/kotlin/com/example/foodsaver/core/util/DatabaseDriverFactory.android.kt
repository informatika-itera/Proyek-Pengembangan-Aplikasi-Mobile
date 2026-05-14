package com.example.foodsaver.core.util

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.foodsaver.data.local.FoodSaverDatabase

actual class DatabaseDriverFactory(
    private val context: Context
) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = FoodSaverDatabase.Schema,
            context = context,
            name = "foodsaver.db"
        )
    }
}
