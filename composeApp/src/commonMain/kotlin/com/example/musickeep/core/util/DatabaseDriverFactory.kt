package com.example.musickeep.core.util

import app.cash.sqldelight.db.SqlDriver

/**
 * Database Driver Factory - expect declaration
 */
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}
