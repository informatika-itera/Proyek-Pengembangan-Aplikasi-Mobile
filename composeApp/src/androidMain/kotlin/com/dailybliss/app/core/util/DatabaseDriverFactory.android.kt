package com.dailybliss.app.core.util

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.dailybliss.app.data.local.BlissDatabase

/**
 * Android implementation of DatabaseDriverFactory
 * 
 * Menggunakan AndroidSqliteDriver yang membungkus SQLite bawaan Android.
 * Database disimpan di internal storage aplikasi.
 */
actual class DatabaseDriverFactory actual constructor(
    private val context: PlatformContext
) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = BlissDatabase.Schema,
            context = context,
            name = "dailybliss_final.db"
        )
    }
}

