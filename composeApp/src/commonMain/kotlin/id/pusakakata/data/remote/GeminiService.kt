package id.pusakakata.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GeminiGenerationConfig = GeminiGenerationConfig()
)

@Serializable
data class GeminiContent(
    val parts: List<GeminiPart>,
    val role: String? = null
)

@Serializable
data class GeminiPart(
    val text: String? = null
)

@Serializable
data class GeminiGenerationConfig(
    val temperature: Double = 0.7,
    val maxOutputTokens: Int = 1000,
    val topP: Double = 0.95
)

@Serializable
data class GeminiResponse(
    val candidates: List<GeminiCandidate> = emptyList(),
    val promptFeedback: GeminiPromptFeedback? = null
)

@Serializable
data class GeminiCandidate(
    val content: GeminiContent? = null,
    val finishReason: String? = null
)

@Serializable
data class GeminiPromptFeedback(
    val blockReason: String? = null
)

class GeminiService(
    private val client: HttpClient,
    private val apiKey: String
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta"
    private val model = "gemini-2.5-flash"

    suspend fun generateDefinition(word: String): String {
        val prompt = """
            Berikan definisi singkat satu paragraf dalam bahasa Indonesia untuk kosa kata: $word. 
            Tentukan juga kategorinya dari pilihan berikut: Umum, Sastra, Arkais.
            Berikan juga satu contoh kalimat penggunaan kata tersebut yang formal atau edukatif.
            Format jawaban harus JSON seperti ini: {"definition": "isi definisi", "category": "Umum/Sastra/Arkais", "example": "contoh kalimat"}.
            Jangan gunakan markdown atau teks lain.
        """.trimIndent()
        return generateContent(prompt).getOrElse { throw it }
    }

    suspend fun generateContent(prompt: String): Result<String> {
        return try {
            val trimmedKey = apiKey.trim()
            if (trimmedKey.isBlank()) {
                return Result.failure(
                    Exception("Gemini API key tidak boleh kosong.")
                )
            }

            if (prompt.isBlank()) {
                return Result.failure(
                    Exception("Prompt tidak boleh kosong.")
                )
            }

            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        role = "user",
                        parts = listOf(
                            GeminiPart(text = prompt)
                        )
                    )
                )
            )

            val httpResponse = client.post(
                "$baseUrl/models/$model:generateContent"
            ) {
                contentType(ContentType.Application.Json)
                parameter("key", trimmedKey)
                setBody(request)
            }

            val rawResponse = httpResponse.bodyAsText()
            val statusCode = httpResponse.status.value

            println("GEMINI_HTTP_STATUS: $statusCode")
            println("GEMINI_RAW_RESPONSE: $rawResponse")

            if (statusCode == 400) {
                return Result.failure(
                    Exception("Request Gemini tidak valid. Detail: $rawResponse")
                )
            }

            if (statusCode == 404) {
                return Result.failure(
                    Exception("Model '$model' tidak ditemukan. Detail: $rawResponse")
                )
            }

            if (statusCode == 429) {
                return Result.failure(
                    Exception("Quota Gemini API tercapai (Error 429).")
                )
            }

            if (statusCode !in 200..299) {
                return Result.failure(
                    Exception("Gemini API error $statusCode: $rawResponse")
                )
            }

            val response = json.decodeFromString<GeminiResponse>(rawResponse)

            val blockReason = response.promptFeedback?.blockReason
            if (!blockReason.isNullOrBlank()) {
                return Result.failure(
                    Exception("Prompt diblokir oleh Gemini. Alasan: $blockReason")
                )
            }

            val candidate = response.candidates.firstOrNull()
            if (candidate == null) {
                return Result.failure(
                    Exception("Gemini tidak mengembalikan kandidat jawaban.")
                )
            }

            if (!candidate.finishReason.isNullOrBlank() && candidate.finishReason != "STOP") {
                return Result.failure(
                    Exception("Gemini menghentikan respons. Alasan: ${candidate.finishReason}")
                )
            }

            val resultText = candidate.content
                ?.parts
                ?.mapNotNull { it.text }
                ?.joinToString(separator = "\n")
                ?.trim()

            if (resultText.isNullOrBlank()) {
                Result.failure(Exception("Gemini tidak memberikan teks respons."))
            } else {
                Result.success(resultText)
            }
        } catch (e: HttpRequestTimeoutException) {
            Result.failure(Exception("Request timeout (Error 504). Periksa koneksi internet."))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Terjadi kesalahan tidak diketahui."))
        }
    }
}
