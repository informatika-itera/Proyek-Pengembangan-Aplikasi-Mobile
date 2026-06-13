package com.example.bookku.data.remote.api

import com.example.bookku.core.network.ApiConfig
import com.example.bookku.data.remote.dto.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.client.statement.*
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.utils.io.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.catch
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

@Serializable
data class ModelListResponse(val models: List<GeminiModelInfo>? = null)

@Serializable
data class GeminiModelInfo(
    val name: String,
    val supportedGenerationMethods: List<String>? = null
)

class GeminiService(private val client: HttpClient) {
    
    private val json = Json { ignoreUnknownKeys = true }
    private var discoveredModel: String? = null
    
    companion object {
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta"
        
        // Daftar model Generasi 3 untuk cadangan (2026)
        private val FALLBACK_MODELS = listOf(
            "gemini-3.5-flash",
            "gemini-3.1-pro",
            "gemini-3-flash",
            "gemini-2.0-flash"
        )
    }

    /**
     * AUTO-DISCOVERY: Secara otomatis mencari model mana yang diizinkan untuk kunci AQ Anda.
     * Ini menghilangkan masalah "Model Not Found".
     */
    private suspend fun getBestAvailableModel(): String {
        if (discoveredModel != null) return discoveredModel!!
        
        val apiKey = ApiConfig.geminiApiKey.trim()
        return try {
            val response: HttpResponse = client.get("$BASE_URL/models") { parameter("key", apiKey) }
            if (response.status == HttpStatusCode.OK) {
                val modelList = response.body<ModelListResponse>()
                // Cari model yang mendukung metode 'generateContent'
                val found = modelList.models?.firstOrNull { 
                    it.supportedGenerationMethods?.contains("generateContent") == true 
                }?.name?.replace("models/", "")
                
                discoveredModel = found ?: FALLBACK_MODELS.first()
                discoveredModel!!
            } else {
                FALLBACK_MODELS.first()
            }
        } catch (_: Exception) {
            FALLBACK_MODELS.first()
        }
    }

    /**
     * DIAGNOSTIK: Gunakan kata 'debug' di chat untuk melihat hasil ini.
     */
    suspend fun debugCheckApi(): String {
        val apiKey = ApiConfig.geminiApiKey.trim()
        return try {
            val response: HttpResponse = client.get("$BASE_URL/models") { parameter("key", apiKey) }
            if (response.status == HttpStatusCode.OK) {
                val modelList = response.body<ModelListResponse>()
                val available = modelList.models?.joinToString { it.name.replace("models/", "") }
                "KUNCI VALID (2026)!\nModel Anda: $available"
            } else {
                val body = response.bodyAsText()
                "API ERROR ${response.status.value}:\n$body"
            }
        } catch (e: Exception) {
            "KONEKSI GAGAL: ${e.message}"
        }
    }
    
    suspend fun generateContent(
        prompt: String,
        systemPrompt: String? = null
    ): Result<String> = runCatching {
        val apiKey = ApiConfig.geminiApiKey.trim()
        val modelName = getBestAvailableModel()
        
        val request = GeminiRequest(
            contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = if (systemPrompt != null) "$systemPrompt\n\n$prompt" else prompt)))),
            generationConfig = GenerationConfig(temperature = 0.7, maxOutputTokens = 1000)
        )
        
        val httpResponse: HttpResponse = client.post("$BASE_URL/models/$modelName:generateContent") {
            contentType(ContentType.Application.Json)
            parameter("key", apiKey)
            setBody(request)
        }

        if (httpResponse.status == HttpStatusCode.OK) {
            httpResponse.body<GeminiResponse>().getTextContent() ?: throw Exception("Respons AI Kosong")
        } else {
            val errorMsg = httpResponse.bodyAsText()
            throw Exception("[$modelName] -> $errorMsg")
        }
    }.recoverCatching { e ->
        if (e.message?.contains("Unable to resolve host") == true) {
            throw Exception("Tidak ada internet di Emulator. Mohon 'Cold Boot' emulator Anda.")
        }
        throw e
    }

    fun generateContentStream(
        prompt: String,
        systemPrompt: String? = null
    ): Flow<String> = flow {
        val apiKey = ApiConfig.geminiApiKey.trim()
        val modelName = getBestAvailableModel()
        
        val request = GeminiRequest(
            contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = if (systemPrompt != null) "$systemPrompt\n\n$prompt" else prompt)))),
            generationConfig = GenerationConfig(temperature = 0.7, maxOutputTokens = 1000)
        )
        
        client.preparePost("$BASE_URL/models/$modelName:streamGenerateContent?alt=sse&key=$apiKey") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.execute { httpResponse ->
            if (httpResponse.status != HttpStatusCode.OK) throw Exception("Error ${httpResponse.status.value}")
            val channel = httpResponse.bodyAsChannel()
            while (!channel.isClosedForRead) {
                val line = channel.readUTF8Line() ?: break
                if (line.startsWith("data: ")) {
                    val jsonString = line.substring(6)
                    try {
                        json.decodeFromString<GeminiResponse>(jsonString).getTextContent()?.let { emit(it) }
                    } catch (_: Exception) {}
                }
            }
        }
    }.catch { _ -> 
        emit("Maaf, terjadi gangguan pada koneksi AI Gemini 3. Silakan coba sesaat lagi.")
    }
}
