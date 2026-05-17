package com.example.tabungin.core.util

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.tabungin.data.local.TabunginDatabase

actual class DatabaseDriverFactory(
    private val context: Context
) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = TabunginDatabase.Schema,
            context = context,
            name = "tabungin.db"
        )
    }
}
