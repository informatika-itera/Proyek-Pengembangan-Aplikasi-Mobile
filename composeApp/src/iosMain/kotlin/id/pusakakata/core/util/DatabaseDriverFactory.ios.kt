package id.pusakakata.core.util

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import id.pusakakata.data.local.PusakaDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = PusakaDatabase.Schema,
            name = "pusaka.db"
        )
    }
}
