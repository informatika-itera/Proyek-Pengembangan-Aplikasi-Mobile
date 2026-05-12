package com.example.pantaujompo.core.util

import app.cash.sqldelight.db.SqlDriver

/**
 * Ini adalah 'expect' (harapan) dari KMP.
 * Kita bilang ke sistem: "Tolong sediakan cara bikin SqlDriver di tiap platform ya!"
 */
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}