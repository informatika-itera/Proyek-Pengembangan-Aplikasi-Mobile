package com.example.todomaster.core.util

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.todomaster.data.local.TaskDatabase

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
            schema = TaskDatabase.Schema,
            context = context,
            name = "todomaster.db"
        )
    }
}
