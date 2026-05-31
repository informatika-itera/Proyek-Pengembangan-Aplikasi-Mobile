package com.example.fintrack.data.remote.api

import com.example.fintrack.core.network.safeApiCall
import com.example.fintrack.core.network.NetworkResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FrankfurterResponse(
    @SerialName("amount") val amount: Double,
    @SerialName("base") val base: String,
    @SerialName("date") val date: String,
    @SerialName("rates") val rates: Map<String, Double>
)

class ExchangeApiService(private val client: HttpClient) {

    companion object {
        private const val BASE_URL = "https://api.frankfurter.app"
        private val ALL_CURRENCIES = listOf("USD", "IDR", "EUR", "GBP", "JPY", "SGD", "AUD")
    }

    suspend fun getLatestRates(
        baseCurrency: String = "USD"
    ): NetworkResult<FrankfurterResponse> = safeApiCall {
        val targetCurrencies = ALL_CURRENCIES.filter { it != baseCurrency }.joinToString(",")
        val url = "$BASE_URL/latest?from=$baseCurrency&to=$targetCurrencies"
        client.get(url).body()
    }
}
