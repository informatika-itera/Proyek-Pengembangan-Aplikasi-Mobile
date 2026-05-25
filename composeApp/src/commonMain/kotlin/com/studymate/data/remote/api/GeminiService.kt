package com.studymate.data.remote.api

import com.studymate.core.network.ApiConfig
import com.studymate.data.remote.dto.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class GeminiService(
    private val client: HttpClient,
    private val apiKey: String
) {
    private val USE_MOCK = false

    suspend fun generateContent(prompt: String): Result<String> {
        return try {
            val response: GeminiResponseDto = client.post("${ApiConfig.GEMINI_BASE_URL}/models/${ApiConfig.GEMINI_MODEL}:generateContent?key=$apiKey") {
                contentType(ContentType.Application.Json)
                setBody(
                    GeminiRequestDto(
                        contents = listOf(
                            ContentDto(parts = listOf(PartDto(text = prompt)))
                        )
                    )
                )
            }.body()

            if (response.error != null) {
                Result.failure(Exception(response.error.message))
            } else {
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (text.isNullOrBlank()) {
                    Result.failure(Exception("Respons Gemini kosong"))
                } else {
                    Result.success(text)
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun refineNote(rawNote: String): Result<String> {
        if (rawNote.isBlank()) return Result.failure(Exception("Catatan tidak boleh kosong"))
        
        if (USE_MOCK) {
            return Result.success("## Ringkasan\nIni adalah ringkasan mock.\n\n## Poin Utama\n- Poin 1\n- Poin 2\n\n## Glosarium\n- Istilah: Definisi")
        }
        
        return generateContent(ApiConfig.Prompts.refineNote(rawNote))
    }

    suspend fun generateQuiz(refinedNote: String): Result<String> {
        if (refinedNote.isBlank()) return Result.failure(Exception("Konten catatan kosong"))
        
        if (USE_MOCK) {
            return Result.success("{\"questions\": [{\"question\": \"Apa itu KMP?\", \"options\": [\"Kotlin Multiplatform\", \"Keyboard Mouse Pad\"], \"correct\": 0, \"explanation\": \"KMP adalah Kotlin Multiplatform\"}]}")
        }
        
        return generateContent(ApiConfig.Prompts.generateQuiz(refinedNote))
    }
}
