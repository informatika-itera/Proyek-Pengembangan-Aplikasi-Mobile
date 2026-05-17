package com.example.tabungin.core.util

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.tabungin.data.local.TabunginDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = TabunginDatabase.Schema,
            name = "tabungin.db"
        )
    }
}
