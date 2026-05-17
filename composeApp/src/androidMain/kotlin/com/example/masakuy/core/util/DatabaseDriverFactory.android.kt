package com.example.masakuy.core.util

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.masakuy.data.local.MasakuyDatabase
import org.koin.android.ext.koin.androidContext

actual class DatabaseDriverFactory {
    actual fun createDriver() = AndroidSqliteDriver(
        MasakuyDatabase.Schema,
        androidContext(),
        "masakuy.db"
    )
}