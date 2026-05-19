package com.example.travelplanner.core.util

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.travelplanner.data.local.TravelPlannerDatabase

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = TravelPlannerDatabase.Schema,
            context = context,
            name = "travel_planner.db"
        )
    }
}