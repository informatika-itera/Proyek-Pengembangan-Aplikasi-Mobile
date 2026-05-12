package com.example.rewind.core.util

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.rewind.data.local.RewindDatabase

/**
 * Android implementation of DatabaseDriverFactory
 * 
 * Menggunakan AndroidSqliteDriver yang membungkus SQLite bawaan Android.
 * Database disimpan di internal storage aplikasi.
 */
actual class DatabaseDriverFactory(
    private val context: Context
) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = RewindDatabase.Schema,
            context = context,
            name = "studyplanner.db"
        )
    }
}
