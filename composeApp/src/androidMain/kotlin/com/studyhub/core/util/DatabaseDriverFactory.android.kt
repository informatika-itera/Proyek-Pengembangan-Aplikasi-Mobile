package com.studyhub.core.util

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.studyhub.data.local.StudyHubDatabase

actual class DatabaseDriverFactory(
    private val context: Context
) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = StudyHubDatabase.Schema,
            context = context,
            name = "studyhub_database_v5.db"
        )
    }
}
