package com.studyhub.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.studyhub.database.StudyHubDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver =
        NativeSqliteDriver(
            schema = StudyHubDatabase.Schema,
            name = "studyhub.db"
        )
}
