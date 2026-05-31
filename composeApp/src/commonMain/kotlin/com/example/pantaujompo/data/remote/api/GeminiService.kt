package com.example.pantaujompo.data.remote.api

import android.graphics.Bitmap
import android.util.Base64
import com.example.pantaujompo.data.remote.dto.GeminiDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.*
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

/**
 * Service untuk memanggil Gemini AI API dari Google.
 * Digunakan untuk analisis nutrisi makanan dan chat asisten kesehatan.
 */
class GeminiService {

    // Kunci API untuk AI Pemindai Nutrisi (Akun A)
    private val apiKeyNutrisi = com.example.pantaujompo.core.network.ApiConfig.geminiApiKeyNutrisi
    private val modelNutrisi = "gemini-3.5-flash"
    private val urlNutrisi = "https://generativelanguage.googleapis.com/v1beta/models/$modelNutrisi:generateContent?key=$apiKeyNutrisi"

    // Kunci API untuk Chat (Akun B)
    private val apiKeyChat = com.example.pantaujompo.core.network.ApiConfig.geminiApiKeyChat
    private val modelChat = "gemini-3.1-flash-lite"
    private val urlChat = "https://generativelanguage.googleapis.com/v1beta/models/$modelChat:generateContent?key=$apiKeyChat"

    // Konfigurasi HTTP Client dengan timeout 60 detik
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true; isLenient = true })
        }
        engine {
            config {
                connectTimeout(60, TimeUnit.SECONDS)
                readTimeout(60, TimeUnit.SECONDS)
                writeTimeout(60, TimeUnit.SECONDS)
            }
        }
    }

    /**
     * Helper untuk memangkas payload token
     */
    private fun resizeBitmap(bitmap: Bitmap, maxSize: Int = 512): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxSize && height <= maxSize) return bitmap
        
        val bitmapRatio = width.toFloat() / height.toFloat()
        var newWidth = maxSize
        var newHeight = maxSize
        if (bitmapRatio > 1) {
            newHeight = (maxSize / bitmapRatio).toInt()
        } else {
            newWidth = (maxSize * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    /**
     * Analisis nutrisi makanan dari teks atau foto.
     * Mengembalikan nama makanan, protein, karbo, lemak, dan info AI.
     */
    suspend fun analisaNutrisi(inputText: String, capturedImage: Bitmap?, profil: String): GeminiDto {
        try {
            val systemInstruction = "Kamu adalah ahli gizi virtual aplikasi Pantau Jompo. Analisis foto/teks makanan dan taksir nilai gizinya (gram) berdasarkan porsi standar hidangan Indonesia."
            val promptTeks = if (inputText.isNotBlank()) "Profil: $profil. Identifikasi gizi: $inputText" else "Profil: $profil. Analisis foto makanan ini."
            
            val partsList = mutableListOf<JsonObject>()
            partsList.add(buildJsonObject { put("text", promptTeks) })

            if (capturedImage != null) {
                val resizedBitmap = resizeBitmap(capturedImage)
                val stream = ByteArrayOutputStream()
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                val base64Image = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
                partsList.add(buildJsonObject {
                    put("inlineData", buildJsonObject {
                        put("mimeType", "image/jpeg")
                        put("data", base64Image)
                    })
                })
            }

            val requestBody = buildJsonObject {
                put("systemInstruction", buildJsonObject {
                    put("parts", buildJsonArray {
                        add(buildJsonObject { put("text", systemInstruction) })
                    })
                })
                put("contents", buildJsonArray {
                    add(buildJsonObject { put("parts", JsonArray(partsList)) })
                })
                put("generationConfig", buildJsonObject {
                    put("temperature", 0.1)
                    put("responseMimeType", "application/json")
                    put("responseSchema", buildJsonObject {
                        put("type", "OBJECT")
                        put("properties", buildJsonObject {
                            put("nama", buildJsonObject { put("type", "STRING") })
                            put("protein", buildJsonObject { put("type", "INTEGER") })
                            put("karbo", buildJsonObject { put("type", "INTEGER") })
                            put("lemak", buildJsonObject { put("type", "INTEGER") })
                            put("info", buildJsonObject { put("type", "STRING") })
                        })
                        put("required", buildJsonArray {
                            add(JsonPrimitive("nama"))
                            add(JsonPrimitive("protein"))
                            add(JsonPrimitive("karbo"))
                            add(JsonPrimitive("lemak"))
                            add(JsonPrimitive("info"))
                        })
                    })
                })
            }

            val response: JsonObject = client.post(urlNutrisi) {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

            val balasan = response["candidates"]?.jsonArray?.get(0)?.jsonObject
                ?.get("content")?.jsonObject
                ?.get("parts")?.jsonArray?.get(0)?.jsonObject
                ?.get("text")?.jsonPrimitive?.content ?: ""

            if (balasan.isBlank()) throw Exception("Respons AI kosong")

            // Pembersih Teks Markdown
            var cleanedBalasan = balasan.trim()
            if (cleanedBalasan.startsWith("```json", ignoreCase = true)) {
                cleanedBalasan = cleanedBalasan.substringAfter("```json").substringBeforeLast("```").trim()
            } else if (cleanedBalasan.startsWith("```")) {
                cleanedBalasan = cleanedBalasan.substringAfter("```").substringBeforeLast("```").trim()
            }

            val jsonObj = Json.parseToJsonElement(cleanedBalasan).jsonObject
            val nama = jsonObj["nama"]?.jsonPrimitive?.content ?: if (inputText.isNotBlank()) inputText else "Makanan Tidak Dikenal"
            val protein = jsonObj["protein"]?.jsonPrimitive?.intOrNull ?: 0
            val karbo = jsonObj["karbo"]?.jsonPrimitive?.intOrNull ?: 0
            val lemak = jsonObj["lemak"]?.jsonPrimitive?.intOrNull ?: 0
            val info = jsonObj["info"]?.jsonPrimitive?.content ?: "Bagus untuk menjaga energi sobat jompo!"
            
            return GeminiDto(nama, protein, karbo, lemak, info)
            
        } catch (e: ResponseException) {
            val errorBody = e.response.bodyAsText()
            println("ERROR API GEMINI: $errorBody")
            val fallbackName = if (inputText.isNotBlank()) inputText else "Tidak Terdeteksi"
            return GeminiDto(
                nama = fallbackName,
                protein = 0, karbo = 0, lemak = 0,
                info = "Gagal memproses (Status ${e.response.status.value}). Coba lagi nanti."
            )
        } catch (e: SerializationException) {
            e.printStackTrace()
            return GeminiDto(
                nama = if (inputText.isNotBlank()) inputText else "Tidak Terdeteksi",
                protein = 0, karbo = 0, lemak = 0,
                info = "AI gagal memformat data gizi (Gagal Parsing JSON)."
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return GeminiDto(
                nama = if (inputText.isNotBlank()) inputText else "Tidak Terdeteksi",
                protein = 0, karbo = 0, lemak = 0,
                info = e.localizedMessage ?: "Terjadi kesalahan tidak terduga."
            )
        }
    }

    /**
     * Fungsi untuk chat dengan AI Asisten Kesehatan.
     * Mengembalikan teks balasan dari AI.
     */
    suspend fun tanyaChat(pesan: String, profil: String): String {
        try {
            val systemInstruction = "Kamu adalah asisten kesehatan AI. Jawab santai, formal dan informatif, dan batasi jawaban MAKSIMAL 3 kalimat."
            val promptTeks = "Profil: $profil. Pertanyaan: $pesan"

            val requestBody = buildJsonObject {
                put("systemInstruction", buildJsonObject {
                    put("parts", buildJsonArray {
                        add(buildJsonObject { put("text", systemInstruction) })
                    })
                })
                put("contents", buildJsonArray {
                    add(buildJsonObject {
                        put("parts", buildJsonArray {
                            add(buildJsonObject { put("text", promptTeks) })
                        })
                    })
                })
                put("generationConfig", buildJsonObject {
                    put("temperature", 0.7)
                    put("maxOutputTokens", 150)
                })
            }

            val response: JsonObject = client.post(urlChat) {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

            val balasan = response["candidates"]?.jsonArray?.get(0)?.jsonObject
                ?.get("content")?.jsonObject
                ?.get("parts")?.jsonArray?.get(0)?.jsonObject
                ?.get("text")?.jsonPrimitive?.content

            if (!balasan.isNullOrBlank()) {
                return balasan
            }
            throw Exception("Respons AI kosong")
            
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is ClientRequestException && e.response.status.value == 429) {
                return "Maaf Sobat Jompo, kuota AI harianku habis (Error 429). Coba lagi nanti ya! 🙏"
            }
            return "Duh, sinyalnya lagi kurang bersahabat nih. Pastikan internetmu stabil dan coba lagi ya! 📡"
        }
    }

    /**
     * AI Insight Harian
     * Menganalisis aktivitas harian dan memberikan rekomendasi kesehatan.
     */
    suspend fun dapatkanInsightHarian(totalJarakKm: Double, totalKalori: Int, durasiMenit: Int, profil: String): String {
        try {
            val promptTeks = """
                Kamu adalah Asisten AI gaul "Pantau Jompo".
                Profil Pengguna: $profil (Sobat Gen Z jompo yang lagi rajin gerak).
                Data aktivitas hari ini:
                - Jarak: ${String.format("%.2f", totalJarakKm)} km
                - Kalori Terbakar: $totalKalori kkal
                - Durasi: $durasiMenit menit
                
                Tugasmu: Berikan 1 paragraf super singkat (maksimal 3 kalimat) berisi pujian seru dan rekomendasi asyik terkait aktivitas ini. Jangan kaku.
            """.trimIndent()

            val requestBody = buildJsonObject {
                put("contents", buildJsonArray {
                    add(buildJsonObject {
                        put("parts", buildJsonArray {
                            add(buildJsonObject { put("text", promptTeks) })
                        })
                    })
                })
                put("generationConfig", buildJsonObject {
                    put("temperature", 0.7)
                    put("maxOutputTokens", 500)
                })
            }

            val response: JsonObject = client.post(urlChat) {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

            val balasan = response["candidates"]?.jsonArray?.get(0)?.jsonObject
                ?.get("content")?.jsonObject
                ?.get("parts")?.jsonArray?.get(0)?.jsonObject
                ?.get("text")?.jsonPrimitive?.content

            if (!balasan.isNullOrBlank()) {
                return balasan
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return "Terus semangat bergerak ya! Jangan lupa istirahat yang cukup dan minum air putih. 💧"
    }
}