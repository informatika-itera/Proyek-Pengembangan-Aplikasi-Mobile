package com.example.musickeep.data.remote.api

import com.example.musickeep.data.remote.dto.GeminiRequest
import com.example.musickeep.data.remote.dto.GeminiResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class GeminiService(
    private val client: HttpClient,
    private val apiKey: String
) {
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent"

    suspend fun generateContent(prompt: String): Result<String?> {
        return try {
            val cleanKey = apiKey.trim().replace("\"", "")
            val response = client.post("$baseUrl?key=$cleanKey") {
                contentType(ContentType.Application.Json)
                setBody(
                    GeminiRequest(
                        contents = listOf(
                            GeminiRequest.Content(
                                parts = listOf(GeminiRequest.Part(text = prompt))
                            )
                        )
                    )
                )
            }
            
            val responseText = response.bodyAsText()
            
            if (response.status == HttpStatusCode.OK) {
                // Kita coba parse manual agar tidak crash jika format berubah
                if (responseText.contains("candidates")) {
                    val geminiResponse: GeminiResponse = response.body()
                    val text = geminiResponse.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    if (text != null) {
                        Result.success(text)
                    } else {
                        Result.failure(Exception("AI Sukses tapi Jawaban Kosong. Cek Logcat!"))
                    }
                } else {
                    Result.failure(Exception("Respon Aneh: $responseText"))
                }
            } else {
                Result.failure(Exception("Server Error ${response.status.value}: $responseText"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
