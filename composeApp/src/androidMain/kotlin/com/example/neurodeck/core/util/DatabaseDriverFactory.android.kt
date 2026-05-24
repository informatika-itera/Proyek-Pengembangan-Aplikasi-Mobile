package com.example.neurodeck.core.util

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.neurodeck.data.local.NeuroDeckDatabase

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver = AndroidSqliteDriver(
        schema = NeuroDeckDatabase.Schema,
        context = context,
        name = "neurodeck.db",
    )
}