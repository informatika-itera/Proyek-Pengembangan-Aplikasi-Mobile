package com.example.bookku.core.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object HttpClientFactory {
    
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = false
        encodeDefaults = true
    }
    
    fun create(enableLogging: Boolean = true): HttpClient {
        return HttpClient {
            install(ContentNegotiation) {
                json(json)
            }
            
            if (enableLogging) {
                install(Logging) {
                    logger = object : Logger {
                        override fun log(message: String) {
                            println("HTTP: $message")
                        }
                    }
                    level = LogLevel.BODY
                }
            }
            
            install(HttpTimeout) {
                requestTimeoutMillis = 60_000    // Tingkatkan ke 60 detik untuk AI
                connectTimeoutMillis = 30_000
                socketTimeoutMillis = 60_000
            }
        }
    }
}

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String, val code: Int? = null) : NetworkResult<Nothing>()
    data object Loading : NetworkResult<Nothing>()
}
