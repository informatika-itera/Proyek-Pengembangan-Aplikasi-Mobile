package com.example.musickeep.core.network

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object HttpClientFactory {
    fun create(enableLogging: Boolean = true): HttpClient {
        return HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }
            if (enableLogging) {
                install(Logging) {
                    level = LogLevel.ALL
                    logger = Logger.DEFAULT
                }
            }
        }
    }
}
