package com.example.pantaujompo

import app.cash.sqldelight.db.SqlDriver

// Definisi expect untuk Android & iOS
expect object ApiConfig {
    val geminiApiKey: String
}

expect class DatabaseDriverFactory() {
    fun createDriver(): SqlDriver
}

expect class DataStoreFactory() {
    fun producePath(): String
}

expect class PlatformLocationProvider {
    fun requestSingleUpdate(callback: (Double, Double) -> Unit)
}