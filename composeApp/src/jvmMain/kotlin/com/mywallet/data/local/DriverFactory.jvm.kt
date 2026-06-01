package com.mywallet.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.mywallet.db.WalletDatabase

import java.io.File

class JvmDriverFactory : DriverFactory {
    override fun createDriver(): SqlDriver {
        val databaseFile = File(System.getProperty("user.home"), "mywallet.db")
        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:${databaseFile.absolutePath}")
        if (!databaseFile.exists()) {
            WalletDatabase.Schema.create(driver)
        }
        return driver
    }
}
