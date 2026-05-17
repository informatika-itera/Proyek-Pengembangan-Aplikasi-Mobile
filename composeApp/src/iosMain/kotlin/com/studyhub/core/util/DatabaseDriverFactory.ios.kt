package com.studyhub.core.util

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.studyhub.data.local.StudyHubDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = StudyHubDatabase.Schema,
            name = "StudyHub.db"
        )
    }
}
