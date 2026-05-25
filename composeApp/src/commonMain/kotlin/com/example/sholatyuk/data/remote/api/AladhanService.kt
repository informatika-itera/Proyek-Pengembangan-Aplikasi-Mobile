package com.example.sholatyuk.data.remote.api

import com.example.sholatyuk.data.remote.dto.AladhanResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class AladhanService(private val client: HttpClient) {

    companion object {
        private const val BASE_URL = "https://api.aladhan.com/v1"
        private const val METHOD = 20
    }

    suspend fun getPrayerTimes(
        latitude: Double,
        longitude: Double,
        date: String
    ): Result<AladhanResponse> = runCatching {
        client.get("$BASE_URL/timings/$date") {
            parameter("latitude", latitude)
            parameter("longitude", longitude)
            parameter("method", METHOD)
        }.body()
    }
}