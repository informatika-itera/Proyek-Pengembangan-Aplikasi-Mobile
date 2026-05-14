package com.example.foodsaver.core.util

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
// Ganti NoteDatabase dengan nama database proyek Anda nantinya, sementara biarkan sesuai template agar build aman
// import com.example.foodsaver.db.FoodSaverDatabase 

/**
 * Implementasi Android untuk DatabaseDriverFactory.
 */
actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = AndroidSqliteDriver.Schema, // Ganti ke FoodSaverDatabase.Schema jika sudah generate
            context = context,
            name = "foodsaver.db"
        )
    }
}
