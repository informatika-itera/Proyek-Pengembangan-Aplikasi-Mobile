package com.example.edumate.core.util

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.edumate.data.local.TaskDatabase // Import TaskDatabase

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = TaskDatabase.Schema, // Ubah schema
            context = context,
            name = "edumate.db" // Ubah nama file db
        )
    }
}