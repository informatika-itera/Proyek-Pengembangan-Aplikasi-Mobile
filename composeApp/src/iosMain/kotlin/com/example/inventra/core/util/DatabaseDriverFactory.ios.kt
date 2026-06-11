package com.example.inventra.core.util

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.inventra.data.local.InventRaDatabase

/**
 * iOS implementation of DatabaseDriverFactory.
 *
 * Fix: referensi diubah dari NoteDatabase → InventRaDatabase.
 * Nama DB diganti ke "InventRa_v2.db" untuk konsistensi dengan Android.
 */
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = InventRaDatabase.Schema,
            name = "InventRa_v3.db"
        )
    }
}