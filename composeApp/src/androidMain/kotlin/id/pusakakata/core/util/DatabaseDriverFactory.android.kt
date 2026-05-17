package id.pusakakata.core.util

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import id.pusakakata.data.local.PusakaDatabase

actual class DatabaseDriverFactory(
    private val context: Context
) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = PusakaDatabase.Schema,
            context = context,
            name = "pusaka.db"
        )
    }
}
