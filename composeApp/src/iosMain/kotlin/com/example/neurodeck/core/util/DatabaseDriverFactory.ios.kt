package com.example.neurodeck.core.util

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.neurodeck.data.local.NeuroDeckDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver = NativeSqliteDriver(
        schema = NeuroDeckDatabase.Schema,
        name = "neurodeck.db",
    )
}