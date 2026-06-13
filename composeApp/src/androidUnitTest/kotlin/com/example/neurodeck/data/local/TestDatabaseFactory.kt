package com.example.neurodeck.data.local

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.cash.sqldelight.db.SqlDriver

/**
 * Factory untuk membuat [NeuroDeckDatabase] in-memory di JVM unit test.
 *
 * Memakai [JdbcSqliteDriver] dengan URL IN_MEMORY — database fresh tiap test,
 * tidak menyentuh disk, dan otomatis hilang saat driver di-close.
 *
 * PENTING: `PRAGMA foreign_keys=ON` di-set manual karena SQLite mematikan
 * foreign key enforcement secara default. Tanpa ini, ON DELETE CASCADE
 * (hapus deck → hapus kartunya) tidak akan jalan dan test cascade gagal.
 */
object TestDatabaseFactory {

    fun createDriver(): SqlDriver {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        NeuroDeckDatabase.Schema.create(driver)
        driver.execute(null, "PRAGMA foreign_keys=ON;", 0)
        return driver
    }

    fun create(driver: SqlDriver): NeuroDeckDatabase = NeuroDeckDatabase(driver)
}
