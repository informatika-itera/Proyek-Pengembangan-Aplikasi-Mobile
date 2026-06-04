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

    override suspend fun generateItinerary(destination: String, duration: String, vibe: String, language: String): String {
        val systemPrompt = """
            Kamu adalah asisten perencana perjalanan AI yang cerdas. Tugasmu adalah membuat itinerary perjalanan harian di $destination.
            Durasi: $duration
            Vibe Liburan: $vibe
            
            PENTING: Hasilkan deskripsi dalam 2 bahasa sekaligus (Indonesia dan English).
            
            Berikan output HARUS dalam bentuk JSON array murni yang berisi objek aktivitas.
            TIDAK BOLEH dibungkus dalam objek apapun (seperti 'day' atau 'itinerary'). Langsung kembalikan array murni [ { ... }, { ... } ].
            Format wajib (array murni):
            [
              {
                "time": "Waktu aktivitas (contoh: '09.00', '12.00')",
                "activity": "Deskripsi aktivitas dalam Bahasa Indonesia",
                "activityEn": "English translation of the activity description",
                "icon": "Emoji yang relevan",
                "priceRange": "Prediksi kisaran harga per orang dalam Rupiah",
                "mapsUrl": "Tautan pencarian Google Maps",
                "placeName": "Nama tempat spesifik persis sama seperti di dalam activity",
                "placeNameEn": "Nama tempat spesifik persis sama seperti di dalam activityEn"
              }
            ]
            
            Jangan berikan teks markdown seperti ```json atau teks pembuka lainnya! HANYA JSON array murni.
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

    override suspend fun translateItinerary(jsonItinerary: String): String {
        val systemPrompt = """
            Kamu adalah penerjemah ahli. Saya akan memberikan sebuah JSON array berisi objek aktivitas perjalanan dalam Bahasa Indonesia.
            Tugasmu adalah menambahkan terjemahan ke dalam Bahasa Inggris untuk setiap aktivitas, TANPA mengubah data lainnya.
            
            JSON Input:
            $jsonItinerary
            
            Berikan output HARUS dalam bentuk JSON array murni. Jangan tambahkan markdown ```json.
            Untuk setiap objek, tambahkan properti:
            "activityEn": "terjemahan dari activity"
            "placeNameEn": "terjemahan dari placeName"
            
            Pastikan properti "time", "icon", "priceRange", "mapsUrl" TIDAK BERUBAH.
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