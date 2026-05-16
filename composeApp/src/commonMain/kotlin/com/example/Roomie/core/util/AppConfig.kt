package com.example.Roomie.core.util

enum class AppEnvironment {
    DEV, PROD
}

object AppConfig {
    // Ganti ke PROD kalau mau rilis beneran
    val current = AppEnvironment.DEV

    val databaseName: String = when (current) {
        AppEnvironment.DEV -> "roomie_dev.db"
        AppEnvironment.PROD -> "roomie.db"
    }

    val baseUrl: String = when (current) {
        AppEnvironment.DEV -> "http://localhost:8080"
        AppEnvironment.PROD -> "https://api.roomie-itera.ac.id"
    }

    val isDebug = current == AppEnvironment.DEV
}
