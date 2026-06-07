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
            name = "studyhub.db"
        )
}
