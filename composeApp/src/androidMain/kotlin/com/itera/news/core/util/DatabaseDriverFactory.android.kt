package com.itera.news.core.util

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.itera.news.data.local.NewsDatabase
import org.koin.java.KoinJavaComponent.getKoin

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        val context: Context = getKoin().get()
        return AndroidSqliteDriver(NewsDatabase.Schema, context, "mbg_news.db")
    }
}