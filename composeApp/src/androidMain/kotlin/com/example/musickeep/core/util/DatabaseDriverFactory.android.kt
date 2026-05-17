package com.example.musickeep.core.util

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.musickeep.data.local.MusicDatabase

/**
 * Android implementation of DatabaseDriverFactory
 */
actual class DatabaseDriverFactory(
    private val context: Context
) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = MusicDatabase.Schema,
            context = context,
            name = "musickeep.db"
        )
    }
}
