package com.studymate.core.util

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.studymate.data.local.StudyMateDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(StudyMateDatabase.Schema, "studymate.db")
    }
}
