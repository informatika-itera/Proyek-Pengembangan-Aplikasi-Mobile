package com.kosthub.app.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.kosthub.app.data.local.KostDatabase
import com.kosthub.app.platform.PlatformContext

actual class DatabaseDriverFactory actual constructor(
    private val platformContext: PlatformContext
) {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(KostDatabase.Schema, "kosthub.db")
    }
}
