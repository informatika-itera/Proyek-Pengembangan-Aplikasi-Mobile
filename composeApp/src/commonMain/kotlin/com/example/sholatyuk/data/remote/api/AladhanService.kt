package com.example.sholatyuk.data.remote.api

import com.example.sholatyuk.data.remote.dto.AladhanResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class AladhanService(private val client: HttpClient) {

    // Mengambil jadwal sholat berdasarkan hari ini dan titik koordinat
    suspend fun getTimingsByLocation(
        lat: Double,
        lng: Double,
        method: Int = 20 // Method 20 adalah standar Kemenag RI
    ): Result<AladhanResponse> {
        return try {
            val response = client.get("https://api.aladhan.com/v1/timings") {
                parameter("latitude", lat)
                parameter("longitude", lng)
                parameter("method", method)
            }

            // Mengubah format JSON dari API menjadi objek Kotlin (AladhanResponse)
            val aladhanResponse: AladhanResponse = response.body()
            Result.success(aladhanResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}