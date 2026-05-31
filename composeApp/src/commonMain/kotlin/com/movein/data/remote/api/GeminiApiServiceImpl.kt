package com.movein.data.remote.api

import com.example.noteai.BuildConfig
import com.movein.data.remote.dto.GeminiResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.buildJsonArray

class GeminiApiServiceImpl(
    private val client: HttpClient
) : GeminiApiService {

    override suspend fun generateActivitySuggestion(mood: String): GeminiResponseDto {
        // Mengambil API Key dari BuildConfig otomatis proyekmu
        val apiKey = BuildConfig.GEMINI_API_KEY
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"

        // Menyusun body JSON manual untuk request ke Google Gemini
        val requestBody = buildJsonObject {
            put("contents", buildJsonArray {
                add(buildJsonObject {
                    put("parts", buildJsonArray {
                        add(buildJsonObject {
                            put("text", "Berikan satu rekomendasi aktivitas positif, singkat, dan seru untuk orang yang sedang merasa $mood. Berikan jawabannya langsung berupa nama aktivitas dan deskripsi singkat saja.")
                        })
                    })
                })
            })
        }

        // Menembak API menggunakan fungsi POST dari Ktor Client
        val response: HttpResponse = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }

        return response.body() // Otomatis diubah menjadi GeminiResponseDto oleh ContentNegotiation
    }
}