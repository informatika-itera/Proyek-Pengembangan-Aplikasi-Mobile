package com.example.travelplanner.core.util

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.travelplanner.data.local.TravelPlannerDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = TravelPlannerDatabase.Schema,
            name = "travel_planner.db"
        )
    }
}