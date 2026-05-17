package com.example.masakuy.data.local.database

import app.cash.sqldelight.db.SqlDriver
import com.example.masakuy.data.local.Recipe

class MasakuyDatabase(sqlDriver: SqlDriver) {
    private val database = com.example.masakuy.data.local.MasakuyDatabase(sqlDriver)

    val recipeQueries = database.recipeQueries

    companion object {
        fun createSchema(): String {
            return """
                CREATE TABLE IF NOT EXISTS recipe (
                    id TEXT NOT NULL PRIMARY KEY,
                    name TEXT NOT NULL,
                    image TEXT NOT NULL,
                    estimatedCost INTEGER NOT NULL,
                    estimatedTime INTEGER NOT NULL,
                    difficulty TEXT NOT NULL,
                    ingredients TEXT NOT NULL,
                    instructions TEXT NOT NULL,
                    isFavorite INTEGER NOT NULL DEFAULT 0,
                    createdAt INTEGER NOT NULL
                );

                CREATE TABLE IF NOT EXISTS favorite (
                    recipeId TEXT NOT NULL PRIMARY KEY,
                    createdAt INTEGER NOT NULL,
                    FOREIGN KEY(recipeId) REFERENCES recipe(id)
                );
            """.trimIndent()
        }
    }
}