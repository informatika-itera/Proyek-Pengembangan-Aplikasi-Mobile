package com.example.inventra.core.util

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.inventra.data.local.InventRaDatabase

/**
 * Android implementation of DatabaseDriverFactory.
 *
 * Menggunakan AndroidSqliteDriver yang membungkus SQLite bawaan Android.
 * Nama DB diganti ke "InventRa_v2.db" untuk menghindari conflict
 * schema migration (local DB adalah pure cache dari Supabase).
 */
actual class DatabaseDriverFactory(
    private val context: Context
) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = InventRaDatabase.Schema,
            context = context,
            name = "InventRa_v3.db"
        )
    }
}