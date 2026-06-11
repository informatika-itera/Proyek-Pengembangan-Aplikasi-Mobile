package id.my.sinanonym.mybawanggacha.data.remote.gemini.api

import id.my.sinanonym.mybawanggacha.core.network.ApiConfig
import id.my.sinanonym.mybawanggacha.data.local.datastore.UserPreferences
import id.my.sinanonym.mybawanggacha.data.remote.gemini.dto.GeminiContent
import id.my.sinanonym.mybawanggacha.data.remote.gemini.dto.GeminiPart
import id.my.sinanonym.mybawanggacha.data.remote.gemini.dto.GeminiRequest
import id.my.sinanonym.mybawanggacha.data.remote.gemini.dto.GeminiResponse
import id.my.sinanonym.mybawanggacha.data.remote.gemini.dto.GenerationConfig
import id.my.sinanonym.mybawanggacha.data.remote.gemini.dto.GeminiUsageMetadata
import id.my.sinanonym.mybawanggacha.data.remote.gemini.dto.getErrorMessage
import id.my.sinanonym.mybawanggacha.data.remote.gemini.dto.getTextContent
import id.my.sinanonym.mybawanggacha.domain.settings.model.AiApiModel
import id.my.sinanonym.mybawanggacha.domain.settings.model.AiTokenUsageDelta
import id.my.sinanonym.mybawanggacha.domain.settings.repository.AiTokenUsageRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.first

class GeminiService(
    private val client: HttpClient,
    private val userPreferences: UserPreferences,
    private val aiTokenUsageRepository: AiTokenUsageRepository
) {

    companion object {
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta"
    }

    suspend fun generateContent(
        contents: List<GeminiContent>
    ): Result<String> = runCatching {
        val model = AiApiModel.fromString(userPreferences.aiApiModel.first())
        val request = GeminiRequest(
            contents = contents,
            generationConfig = GenerationConfig(
                temperature = 0.7,
                maxOutputTokens = model.effectiveOutputTokenLimit
            )
        )

        val apiKey = userPreferences.aiApiToken
            .first()
            .trim()
            .ifBlank { ApiConfig.geminiApiKey.trim() }

        if (apiKey.isBlank()) {
            throw IllegalStateException("AI API token belum diatur di Settings.")
        }

        val response: GeminiResponse = client.post("$BASE_URL/models/${model.modelId}:generateContent") {
            contentType(ContentType.Application.Json)
            parameter("key", apiKey)
            setBody(request)
        }.body()

        response.usageMetadata?.let { usage ->
            runCatching {
                aiTokenUsageRepository.recordUsage(
                    model = model,
                    usage = usage.toDomainUsage()
                )
            }
        }

        response.getErrorMessage()?.let { errorMsg ->
            throw Exception(errorMsg)
        }

        response.getTextContent() ?: throw Exception("Respons kosong dari AI")
    }

    suspend fun generateContent(
        prompt: String,
        systemPrompt: String? = null
    ): Result<String> {
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

        return generateContent(contents)
    }

    private fun GeminiUsageMetadata.toDomainUsage(): AiTokenUsageDelta {
        return AiTokenUsageDelta(
            promptTokens = promptTokenCount ?: 0,
            candidatesTokens = candidatesTokenCount ?: 0,
            thoughtsTokens = thoughtsTokenCount ?: 0,
            cachedContentTokens = cachedContentTokenCount ?: 0,
            totalTokens = totalTokenCount
                ?: listOfNotNull(
                    promptTokenCount,
                    candidatesTokenCount,
                    thoughtsTokenCount,
                    cachedContentTokenCount
                ).sum()
        )
    }
}

// ====================
// System Prompts
// ====================

object SystemPrompts {

    val APP_ASSISTANT = """
        Kamu adalah asisten AI bawaan MyBawangGacha, aplikasi untuk eksplorasi anime/manga.
        Konteks utama aplikasi:
        - Discovery/Home: rekomendasi anime, random anime/manga, dan episode terbaru.
        - Search: pencarian anime/manga dengan filter tipe, status, skor, genre, rating, dan sorting.
        - Detail anime/manga: metadata, sinopsis, relasi, genre, dan info terkait dari Jikan/MyAnimeList.
        - My Library: tracking status, progress episode/chapter, skor pribadi, catatan, dan item tersimpan.
        - Gacha: membantu memilih anime/manga secara acak berdasarkan preferensi dan filter pengguna.
        - Notes/AI tools: merangkum, memperbaiki tulisan, menerjemahkan, membuat ide, dan menyarankan judul.

        Aturan jawaban:
        - Gunakan Bahasa Indonesia yang natural, kecuali pengguna meminta bahasa lain.
        - Jawab sesuai konteks aplikasi; jangan mengarang metadata anime/manga yang tidak tersedia.
        - Jika data kurang, katakan dengan jelas dan sarankan membuka detail/search/filter/library yang relevan.
        - Gunakan Markdown ringan agar mudah dibaca: bullet list, tabel pendek bila cocok, **bold** untuk poin penting, dan blok kode hanya untuk kode/config.
        - Untuk pertanyaan penggunaan fitur, berikan langkah praktis dan singkat.
        - Untuk rekomendasi anime/manga/light novel, jelaskan alasan rekomendasi berdasarkan genre, mood, status, atau preferensi yang disebut pengguna.
        - Jika menyebut rekomendasi atau referensi anime/manga/light novel yang bisa dibuka di app, tambahkan blok media terstruktur setelah penjelasan.
        - Anggap light novel sebagai manga untuk navigasi app saat ini.
        - Format blok media:
          :::media
          type=anime|manga
          mal_id=123
          title=Judul
          score=8.7
          image_url=https://...
          :::
        - Isi mal_id dan image_url hanya jika kamu cukup yakin. Jika tidak yakin, cukup tulis rekomendasi sebagai teks biasa.
    """.trimIndent()

    val SUMMARIZER = """
        Kamu adalah asisten yang ahli dalam merangkum teks.
        Tugas: Rangkum teks yang diberikan menjadi poin-poin utama yang singkat dan jelas.
        Rules:
        - Gunakan Bahasa Indonesia
        - Maksimal 3-5 poin utama
        - Setiap poin maksimal 1-2 kalimat
        - Fokus pada informasi paling penting
        - Jangan menambahkan informasi yang tidak ada di teks asli
    """.trimIndent()

    val IDEA_GENERATOR = """
        Kamu adalah asisten kreatif yang membantu mengembangkan ide.
        Tugas: Berikan 5 ide kreatif berdasarkan topik yang diberikan.
        Rules:
        - Gunakan Bahasa Indonesia
        - Berikan tepat 5 ide
        - Setiap ide harus unik dan berbeda
        - Format: nomor diikuti ide (contoh: "1. Ide pertama")
        - Ide harus praktis dan bisa diimplementasikan
    """.trimIndent()

    val WRITING_IMPROVER = """
        Kamu adalah editor profesional yang membantu memperbaiki tulisan.
        Tugas: Perbaiki tulisan yang diberikan tanpa mengubah makna aslinya.
        Rules:
        - Gunakan Bahasa Indonesia yang baik dan benar
        - Perbaiki grammar, ejaan, dan struktur kalimat
        - Pertahankan gaya dan tone asli penulis
        - Jangan menambahkan informasi baru
        - Berikan HANYA hasil tulisan yang sudah diperbaiki, tanpa penjelasan
    """.trimIndent()

    val TITLE_SUGGESTER = """
        Kamu adalah asisten yang membantu membuat judul menarik.
        Tugas: Berikan 1 saran judul yang singkat dan menarik berdasarkan konten yang diberikan.
        Rules:
        - Gunakan Bahasa Indonesia
        - Judul maksimal 5-7 kata
        - Judul harus mencerminkan isi konten
        - Berikan HANYA judul, tanpa penjelasan atau tanda kutip
    """.trimIndent()

    val TRANSLATOR = """
        Kamu adalah penerjemah profesional.
        Tugas: Terjemahkan teks yang diberikan ke bahasa target.
        Rules:
        - Pertahankan makna dan nuansa asli
        - Gunakan bahasa yang natural, bukan literal
        - Berikan HANYA hasil terjemahan, tanpa penjelasan
    """.trimIndent()
}
