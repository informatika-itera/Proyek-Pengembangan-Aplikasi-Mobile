package com.movein.data.remote.api

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
    private val client: HttpClient,
    private val apiKey: String
) : GeminiApiService {

    override suspend fun generateActivitySuggestion(mood: String): GeminiResponseDto {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"

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

        val response: HttpResponse = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }

        return response.body()
    }
}