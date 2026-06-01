package com.mywallet.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class CurrencyResponse(
    val base: String,
    val rates: Map<String, Double>
)

interface CurrencyService {
    suspend fun getExchangeRates(): CurrencyResponse
}

class CurrencyServiceImpl(private val client: HttpClient) : CurrencyService {
    override suspend fun getExchangeRates(): CurrencyResponse {
        return client.get("https://api.exchangerate-api.com/v4/latest/IDR").body()
    }
}

val networkModule = kotlinx.serialization.json.Json {
    ignoreUnknownKeys = true
    prettyPrint = true
    isLenient = true
}

fun createHttpClient() = HttpClient {
    install(ContentNegotiation) {
        json(networkModule)
    }
    install(Logging) {
        level = LogLevel.INFO
    }
}
