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

    private val apiKey = com.example.travelplanner.core.network.ApiConfig.geminiApiKey
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=$apiKey"

    override suspend fun generateItinerary(destination: String, duration: String, vibe: String): String {
        val systemPrompt = """
            Kamu adalah asisten perencana perjalanan AI yang cerdas. Tugasmu adalah membuat itinerary perjalanan harian di $destination.
            Durasi: $duration
            Vibe Liburan: $vibe
            
            Berikan output dalam bentuk format JSON raw yang rapi berisi array objek dengan properti berikut:
            - 'time': Waktu aktivitas (contoh: '09.00', '12.00')
            - 'activity': Deskripsi lengkap aktivitas perjalanan atau kuliner harian (contoh: 'Makan malam santai dengan kulineran lokal khas di Merdeka Walk Medan')
            - 'icon': Emoji yang relevan dengan aktivitas tersebut (contoh: '🍽️', '🌳')
            - 'priceRange': Prediksi kisaran harga/biaya masuk/makan per orang dalam Rupiah (contoh: 'Rp 20rb - 50rb', 'Rp 100rb - 150rb', atau 'Gratis')
            - 'mapsUrl': Tautan pencarian Google Maps untuk nama tempat spesifik tersebut (contoh: 'https://www.google.com/maps/search/?api=1&query=Merdeka+Walk+Medan')
            - 'placeName': Nama tempat spesifik yang dikunjungi yang terdapat di dalam deskripsi aktivitas (contoh: 'Merdeka Walk Medan'). Suku kata ini HARUS tertulis persis sama dengan kata yang ada di dalam 'activity' agar aplikasi bisa mendeteksi dan menjadikannya hyperlink.
            
            Jangan berikan teks markdown seperti ```json atau teks pembuka lainnya! Hanya return string JSON murni berbentuk array objek.
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