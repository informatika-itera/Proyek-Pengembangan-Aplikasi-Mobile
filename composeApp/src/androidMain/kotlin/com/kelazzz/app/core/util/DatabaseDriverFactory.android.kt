package com.kelazzz.app.core.util

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.kelazzz.app.data.local.KelazZzDatabase

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
            schema = KelazZzDatabase.Schema,
            context = context,
            name = "kelazzz.db"
        )
    }
}
