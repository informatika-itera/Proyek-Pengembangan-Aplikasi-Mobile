package com.example.gamenews.core.util

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.gamenews.GameDatabase //

actual class DatabaseDriverFactory(
    private val context: Context
) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = GameDatabase.Schema, //
            context = context,
            name = "gamenews.db" //
        )
    }
}