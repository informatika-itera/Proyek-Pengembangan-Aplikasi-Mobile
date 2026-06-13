package com.example.fitgen.data.remote.api

import com.example.fitgen.BuildKonfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.encodeBase64
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class GeminiService(private val client: HttpClient) {

    private val apiKey = BuildKonfig.GEMINI_API_KEY
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"

    suspend fun analyzeImage(prompt: String, imageBytes: ByteArray): String {
        try {
            val base64Image = imageBytes.encodeBase64()

            val requestBody = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        parts = listOf(
                            Part(text = prompt),
                            Part(
                                inlineData = InlineData(
                                    mimeType = "image/jpeg",
                                    data = base64Image
                                )
                            )
                        )
                    )
                )
            )

            // Simpan respons ke dalam variabel HttpResponse terlebih dahulu
            val httpResponse: HttpResponse = client.post("$baseUrl?key=$apiKey") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }

            // Cek apakah HTTP statusnya berhasil (200-299)
            if (httpResponse.status.value in 200..299) {
                val response = httpResponse.body<GeminiResponse>()
                val replyText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

                return replyText ?: throw Exception("Respons AI berhasil diterima, tapi teks jawaban kosong.")
            } else {
                // Jika gagal, tangkap pesan error ASLI dari server Google
                val errorBody = httpResponse.bodyAsText()
                throw Exception("API Error (${httpResponse.status.value}): $errorBody")
            }

        } catch (e: Exception) {
            val msg = e.message ?: ""
            if (msg.contains("resolve host", ignoreCase = true) || 
                msg.contains("UnknownHostException", ignoreCase = true) || 
                msg.contains("ConnectException", ignoreCase = true) ||
                msg.contains("failed to connect", ignoreCase = true)) {
                throw Exception("Tidak ada koneksi internet. Silakan periksa jaringan Anda.")
            }
            throw e
        }
    }
}

// =========================================================================
// Data Classes (DTO)
// =========================================================================

@Serializable
private data class GeminiRequest(
    val contents: List<GeminiContent>
)

@Serializable
private data class GeminiContent(
    val parts: List<Part>
)

@Serializable
private data class Part(
    val text: String? = null,
    @SerialName("inline_data")
    val inlineData: InlineData? = null
)

@Serializable
private data class InlineData(
    @SerialName("mime_type")
    val mimeType: String,
    val data: String
)

@Serializable
private data class GeminiResponse(
    val candidates: List<Candidate>? = null
)

@Serializable
private data class Candidate(
    val content: GeminiContent? = null
)