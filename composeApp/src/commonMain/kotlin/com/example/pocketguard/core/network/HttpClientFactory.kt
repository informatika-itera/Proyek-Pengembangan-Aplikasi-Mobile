package com.example.pocketguard.core.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Factory untuk membuat HttpClient yang sudah dikonfigurasi
 * 
 * HttpClient di KMP menggunakan engine yang berbeda per platform:
 * - Android: OkHttp
 * - iOS: Darwin (NSURLSession)
 * 
 * Plugin yang digunakan:
 * - ContentNegotiation: Serialize/deserialize JSON
 * - Logging: Log request/response untuk debugging
 * - HttpTimeout: Set timeout untuk request
 */
object HttpClientFactory {
    
    /**
     * JSON configuration untuk serialization
     */
    private val json = Json {
        ignoreUnknownKeys = true      // Ignore keys yang tidak ada di data class
        isLenient = true              // Lebih toleran terhadap format JSON
        prettyPrint = false           // Tidak perlu pretty print untuk production
        encodeDefaults = true         // Include default values saat serialize
    }
    
    /**
     * Create configured HttpClient
     * 
     * @param enableLogging Enable logging untuk debugging (disable di production)
     */
    fun create(enableLogging: Boolean = true): HttpClient {
        return HttpClient {
            // JSON Serialization
            install(ContentNegotiation) {
                json(json)
            }
            
            // Logging (untuk development/debugging)
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
            
            // Timeout configuration
            install(HttpTimeout) {
                requestTimeoutMillis = 30_000    // 30 seconds
                connectTimeoutMillis = 15_000    // 15 seconds
                socketTimeoutMillis = 30_000     // 30 seconds
            }
        }
    }
}

/**
 * Sealed class untuk hasil network operation
 */
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String, val code: Int? = null) : NetworkResult<Nothing>()
    data object Loading : NetworkResult<Nothing>()
}

/**
 * Extension function untuk safe API call
 */
suspend fun <T> safeApiCall(block: suspend () -> T): NetworkResult<T> {
    return try {
        NetworkResult.Success(block())
    } catch (e: Exception) {
        NetworkResult.Error(e.message ?: "Unknown error")
    }
}
