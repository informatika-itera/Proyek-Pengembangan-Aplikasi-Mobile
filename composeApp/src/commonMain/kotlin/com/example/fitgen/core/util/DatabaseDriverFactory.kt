package com.example.fitgen.core.util

import app.cash.sqldelight.db.SqlDriver

/**
 * Database Driver Factory - expect declaration
 * 
 * SQLDelight membutuhkan SqlDriver yang berbeda untuk setiap platform:
 * - Android: AndroidSqliteDriver
 * - iOS: NativeSqliteDriver
 * 
 * Kita menggunakan expect/actual pattern untuk menyediakan
 * implementasi yang tepat di setiap platform.
 */
expect class DatabaseDriverFactory {
    /**
     * Create SqlDriver instance
     * @return SqlDriver untuk platform ini
     */
    fun createDriver(): SqlDriver
}
