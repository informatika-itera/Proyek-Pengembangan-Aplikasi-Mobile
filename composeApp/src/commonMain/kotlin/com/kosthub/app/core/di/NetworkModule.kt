package com.kosthub.app.core.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import com.kosthub.app.data.remote.api.ApiService
import com.kosthub.app.data.remote.api.ApiServiceImpl

val networkModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }
            install(Logging) {
                level = LogLevel.ALL
            }
        }
    }
    
    single<ApiService> {
        // Default to localhost, can be customized or passed dynamically
        ApiServiceImpl(client = get(), baseUrl = "http://localhost:8787")
    }
}
