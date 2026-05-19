package com.example.travelplanner.data.repository

import com.example.travelplanner.domain.repository.AIRepository
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.*

class AIRepositoryImpl(
    private val client: HttpClient
) : AIRepository {

    // Sir, pastikan Anda menaruh API Key Anda di local.properties atau proxy backend Anda nanti.
    private val apiKey = "YOUR_GEMINI_API_KEY"
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"

    override suspend fun generateItinerary(destination: String, duration: String, vibe: String): String {
        val systemPrompt = """
            Kamu adalah asisten perencana perjalanan AI yang cerdas. Tugasmu adalah membuat itinerary perjalanan harian.
            Kota Tujuan: $destination
            Durasi: $duration
            Vibe Liburan: $vibe
            
            Berikan output dalam bentuk format JSON raw yang rapi berisi array objek dengan properti: 'time', 'activity', dan 'icon' (berupa emoji yang relevan). Jangan berikan teks markdown seperti ```json atau teks pembuka lainnya! Hanya return string JSON murni.
        """.trimIndent()

        return try {
            val response = makeGeminiApiCall(systemPrompt)
            parseGeminiResponse(response)
        } catch (e: Exception) {
            "{\"error\": \"Gagal menyusun itinerary: ${e.message}\"}"
        }
    }

    override suspend fun extractExpenseFromText(conversationalText: String): String {
        val systemPrompt = """
            Kamu adalah asisten keuangan terpercaya. Ekstrak data pengeluaran dari teks percakapan berikut:
            "$conversationalText"
            
            Jika ada lebih dari satu pengeluaran, pisahkan menjadi beberapa objek.
            Kembalikan HANYA format JSON array murni tanpa pembungkus markdown ```json seperti berikut:
            [
              {
                "nama_item": "String",
                "nominal": Integer,
                "kategori": "Transportasi/Konsumsi/Penginapan/Hiburan/Lainnya"
              }
            ]
            Jika tidak ada nominal atau item tidak valid, kembalikan array kosong []. Jangan berasumsi atau berhalusinasi.
        """.trimIndent()

        return try {
            val response = makeGeminiApiCall(systemPrompt)
            parseGeminiResponse(response)
        } catch (e: Exception) {
            "[]"
        }
    }

    private suspend fun makeGeminiApiCall(prompt: String): String {
        // Membangun request payload sesuai spesifikasi resmi REST API Gemini
        val requestBody = JsonObject(
            mapOf(
                "contents" to JsonArray(
                    listOf(
                        JsonObject(
                            mapOf(
                                "parts" to JsonArray(
                                    listOf(
                                        JsonObject(mapOf("text" to JsonPrimitive(prompt)))
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )

        val response: HttpResponse = client.post(baseUrl) {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }

        return response.bodyAsText()
    }

    private fun parseGeminiResponse(responseBody: String): String {
        val jsonElement = Json.parseToJsonElement(responseBody)
        // Ekstraksi rekursif ke dalam struktur internal JSON Gemini response object: candidates[0].content.parts[0].text
        val textResult = jsonElement.jsonObject["candidates"]
            ?.jsonArray?.get(0)
            ?.jsonObject?.get("content")
            ?.jsonObject?.get("parts")
            ?.jsonArray?.get(0)
            ?.jsonObject?.get("text")
            ?.jsonPrimitive?.content

        return textResult ?: ""
    }
}