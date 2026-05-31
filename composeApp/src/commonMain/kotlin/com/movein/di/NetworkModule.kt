package com.movein.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.HttpTimeout
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule = module {
    single {
        HttpClient {
            // Konfigurasi agar Ktor otomatis mengubah JSON dari internet menjadi objek Kotlin
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true // Jika API mengirim data tambahan, aplikasi tidak akan crash
                    prettyPrint = true
                    isLenient = true
                })
            }

            // Menampilkan log request internet menggunakan fungsi print bawaan Kotlin (Pasti Aman & Anti Error)
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println("KTOR_LOG: $message")
                    }
                }
                level = LogLevel.INFO
            }

            // Pengaturan batas waktu tunggu (Timeout) agar aplikasi tidak hang jika internet lemot
            install(HttpTimeout) {
                requestTimeoutMillis = 15000L // 15 detik
                connectTimeoutMillis = 15000L // 15 detik
            }
        }
    }
}