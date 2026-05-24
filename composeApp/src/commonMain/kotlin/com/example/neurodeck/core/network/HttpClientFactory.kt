package com.example.neurodeck.core.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Factory untuk membuat HttpClient (Ktor) yang shared antara semua platform.
 *
 * Konfigurasi default:
 * - ContentNegotiation + JSON: parse response Gemini API
 *   - ignoreUnknownKeys: forward-compat kalau Gemini tambah field baru
 *   - isLenient: lebih toleran parsing
 *   - explicitNulls=false: jangan kirim field null ke API
 * - HttpTimeout: Gemini bisa lambat (terutama prompt panjang), kasih buffer
 * - Logging: dimatikan di production, nyala di debug
 *
 * Dipakai oleh Koin networkModule, di-inject ke GeminiService nanti.
 */
object HttpClientFactory {

    fun create(enableLogging: Boolean = true): HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    prettyPrint = false
                    explicitNulls = false
                },
            )
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 10_000
            socketTimeoutMillis = 30_000
        }

        if (enableLogging) {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println("[Ktor] $message")
                    }
                }
                level = LogLevel.INFO
            }
        }
    }
}