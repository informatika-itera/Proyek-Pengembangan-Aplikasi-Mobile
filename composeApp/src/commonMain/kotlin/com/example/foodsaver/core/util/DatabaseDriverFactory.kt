package com.example.foodsaver.core.util

import app.cash.sqldelight.db.SqlDriver

/**
 * Factory untuk membuat SQLDelight Driver berdasarkan platform (Android/iOS).
 */
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}
