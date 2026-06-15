package com.studyhub.data.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.studyhub.database.StudyHubDatabase

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver =
        AndroidSqliteDriver(
            schema = StudyHubDatabase.Schema,
            context = context,
            name = "studyhub.db",
            callback = object : AndroidSqliteDriver.Callback(StudyHubDatabase.Schema) {
                override fun onOpen(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                    super.onOpen(db)
                    try {
                        db.execSQL("ALTER TABLE TaskEntity ADD COLUMN colorHex TEXT NOT NULL DEFAULT '#85C1A3'")
                    } catch (e: Exception) {
                        // Column already exists or other error
                    }
                    try {
                        db.execSQL("ALTER TABLE NotifHistoryEntity ADD COLUMN type TEXT NOT NULL DEFAULT 'TASK'")
                    } catch (e: Exception) {
                        // Column already exists or other error
                    }
                }
            }
        )
}
