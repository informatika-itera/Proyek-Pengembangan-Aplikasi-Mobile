package com.kelazzz.app.data.remote.gemini

import com.kelazzz.app.core.network.ApiConfig
import kotlinx.serialization.Serializable
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

// ==================== DTOs ====================

@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GenerationConfig? = null,
    val safetySettings: List<SafetySetting>? = null
)

@Serializable
data class GeminiContent(
    val parts: List<GeminiPart>,
    val role: String = "user"
)

@Serializable
data class GeminiPart(
    val text: String
)

@Serializable
data class GenerationConfig(
    val temperature: Double = 0.7,
    val maxOutputTokens: Int = 1000,
    val topP: Double = 0.95,
    val topK: Int = 40
)

@Serializable
data class SafetySetting(
    val category: String,
    val threshold: String
)

@Serializable
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null,
    val promptFeedback: PromptFeedback? = null,
    val error: GeminiError? = null
)

@Serializable
data class GeminiCandidate(
    val content: GeminiContent,
    val finishReason: String? = null,
    val index: Int = 0,
    val safetyRatings: List<SafetyRating>? = null
)

@Serializable
data class SafetyRating(
    val category: String,
    val probability: String
)

@Serializable
data class PromptFeedback(
    val safetyRatings: List<SafetyRating>? = null,
    val blockReason: String? = null
)

@Serializable
data class GeminiError(
    val code: Int,
    val message: String,
    val status: String
)

// ==================== HELPER EXTENSIONS ====================

fun GeminiResponse.getTextContent(): String? {
    return candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
}

fun GeminiResponse.isBlocked(): Boolean {
    return promptFeedback?.blockReason != null
}

fun GeminiResponse.getErrorMessage(): String? {
    return error?.message ?: if (isBlocked()) {
        "Konten diblokir: ${promptFeedback?.blockReason}"
    } else {
        null
    }
}

// ==================== SERVICE ====================

/**
 * Gemini API Service untuk fitur AI di KelazZz
 * 
 * Digunakan untuk:
 * - AI Early Warning kehadiran
 * - AI Chatbot asisten akademik
 */
class GeminiService(private val client: HttpClient) {
    
    companion object {
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta"
        private const val MODEL = "gemini-2.0-flash"
    }
    
    suspend fun generateContent(
        prompt: String,
        systemPrompt: String? = null
    ): Result<String> = runCatching {
        val contents = mutableListOf<GeminiContent>()
        
        if (systemPrompt != null) {
            contents.add(
                GeminiContent(
                    parts = listOf(GeminiPart(text = systemPrompt)),
                    role = "user"
                )
            )
            contents.add(
                GeminiContent(
                    parts = listOf(GeminiPart(text = "Baik, saya akan mengikuti instruksi tersebut.")),
                    role = "model"
                )
            )
        }
        
        contents.add(
            GeminiContent(
                parts = listOf(GeminiPart(text = prompt)),
                role = "user"
            )
        )
        
        val request = GeminiRequest(
            contents = contents,
            generationConfig = GenerationConfig(
                temperature = 0.7,
                maxOutputTokens = 1000
            )
        )
        
        val response: GeminiResponse = client.post("$BASE_URL/models/$MODEL:generateContent") {
            contentType(ContentType.Application.Json)
            parameter("key", ApiConfig.geminiApiKey)
            setBody(request)
        }.body()
        
        response.getErrorMessage()?.let { errorMsg ->
            throw Exception(errorMsg)
        }
        
        response.getTextContent() ?: throw Exception("Respons kosong dari AI")
    }
}

// ==================== SYSTEM PROMPTS FOR KELAZZZ ====================

object SystemPrompts {
    
    val ATTENDANCE_ANALYZER = """
        Kamu adalah asisten akademik yang menganalisis data kehadiran mahasiswa.
        Tugas: Analisis persentase kehadiran per mata kuliah dan berikan peringatan dini.
        Rules:
        - Gunakan Bahasa Indonesia
        - Berikan peringatan jika kehadiran di bawah 80%
        - Hitung berapa kali lagi mahasiswa bisa absen
        - Berikan saran yang actionable
        - Format: mata kuliah, persentase, status (Aman/Warning/Bahaya), saran
    """.trimIndent()
    
    val ACADEMIC_ASSISTANT = """
        Kamu adalah asisten akademik ITERA yang membantu mahasiswa.
        Tugas: Jawab pertanyaan tentang aturan dan prosedur akademik ITERA.
        Rules:
        - Gunakan Bahasa Indonesia
        - Jawab dengan singkat dan jelas
        - Berdasarkan aturan akademik yang umum berlaku di ITERA
        - Jika tidak yakin, katakan bahwa mahasiswa sebaiknya konfirmasi ke bagian akademik
        - Bersikap ramah dan supportive
    """.trimIndent()
}
