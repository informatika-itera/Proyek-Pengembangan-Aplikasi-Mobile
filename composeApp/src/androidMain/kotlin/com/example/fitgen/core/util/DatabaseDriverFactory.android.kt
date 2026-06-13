package com.example.fitgen.core.util

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.fitgen.data.local.FitGenDatabase

/**
 * Android implementation of DatabaseDriverFactory
 * 
 * Menggunakan AndroidSqliteDriver yang membungkus SQLite bawaan Android.
 * Database disimpan di internal storage aplikasi.
 */
actual class DatabaseDriverFactory(
    private val context: Context
) {
    actual fun createDriver(): SqlDriver {
        val driver = AndroidSqliteDriver(
            schema = FitGenDatabase.Schema,
            context = context,
            name = "fitgen.db"
        )
        try {
            driver.execute(null, "CREATE TABLE IF NOT EXISTS CustomRoutineEntity (id INTEGER PRIMARY KEY AUTOINCREMENT, nama_rutinitas TEXT NOT NULL, created_at INTEGER NOT NULL);", 0)
            driver.execute(null, "CREATE TABLE IF NOT EXISTS RoutineExerciseEntity (id INTEGER PRIMARY KEY AUTOINCREMENT, routine_id INTEGER NOT NULL, nama_gerakan TEXT NOT NULL, body_part TEXT NOT NULL, gif_url TEXT NOT NULL, instructions TEXT NOT NULL, FOREIGN KEY(routine_id) REFERENCES CustomRoutineEntity(id) ON DELETE CASCADE);", 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return driver
    }
}
