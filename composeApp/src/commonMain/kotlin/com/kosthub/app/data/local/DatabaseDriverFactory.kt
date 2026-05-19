package com.kosthub.app.data.local

import app.cash.sqldelight.db.SqlDriver
import com.kosthub.app.platform.PlatformContext

expect class DatabaseDriverFactory(platformContext: PlatformContext) {
    fun createDriver(): SqlDriver
}
