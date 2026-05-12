package com.example.pantaujompo.core.util

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.pantaujompo.data.local.PantauJompoDatabase
import com.example.pantaujompo.PantauJompoApplication

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = PantauJompoDatabase.Schema,
            context = PantauJompoApplication.appContext, // Ambil dari jalur pintas
            name = "pantaujompo.db"
        )
    }
}