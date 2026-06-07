package com.example.tripmate.data.repository

import com.example.tripmate.data.remote.api.GeminiService
import com.example.tripmate.domain.repository.AIRepository
import com.example.tripmate.domain.repository.WritingStyle

open class AIRepositoryImpl(
    private val geminiService: GeminiService
) : AIRepository {

    override suspend fun summarize(text: String): Result<String> {
        return geminiService.generateContent(
            prompt = text,
            systemPrompt = "Kamu adalah asisten yang ahli merangkum. Rangkum teks berikut menjadi poin-poin utama dalam Bahasa Indonesia."
        )
    }

    override suspend fun generateIdeas(topic: String): Result<List<String>> {
        return geminiService.generateContent(
            prompt = "Topik: $topic",
            systemPrompt = "Berikan 5 ide kreatif berdasarkan topik. Format: tiap ide di baris baru diawali angka (1. 2. dst). Gunakan Bahasa Indonesia."
        ).map { response ->
            response.lines()
                .filter { it.matches(Regex("^\\d+\\..*")) }
                .map { it.replaceFirst(Regex("^\\d+\\.\\s*"), "") }
                .ifEmpty { listOf(response) }
        }
    }

    override suspend fun improveWriting(text: String, style: WritingStyle): Result<String> {
        return geminiService.generateContent(
            prompt = text,
            systemPrompt = "${style.prompt}. Berikan HANYA hasil perbaikan tanpa penjelasan."
        )
    }

    override suspend fun translate(text: String, targetLanguage: String): Result<String> {
        return geminiService.generateContent(
            prompt = text,
            systemPrompt = "Terjemahkan teks berikut ke $targetLanguage. Berikan HANYA hasil terjemahan."
        )
    }

    override suspend fun chat(message: String): Result<String> {
        return geminiService.generateContent(prompt = message)
    }

    override suspend fun suggestTitle(content: String): Result<String> {
        return geminiService.generateContent(
            prompt = content,
            systemPrompt = "Sarankan 1 judul singkat (maks 7 kata) untuk konten berikut. Berikan HANYA judul, tanpa tanda kutip."
        )
    }

    open suspend fun generateItinerary(
        destination: String,
        duration: Int,
        budget: Double,
        interests: String
    ): Result<String> {
        val budgetFormatted = budget.toLong().toString()
            .reversed().chunked(3).joinToString(".").reversed()

        val prompt = """
            Destinasi: $destination
            Durasi: $duration hari
            Budget: Rp $budgetFormatted
            Minat: $interests
        """.trimIndent()

        val systemPrompt = """
            Kamu adalah travel planner. Buat itinerary SINGKAT dan PADAT.
            
            ATURAN KETAT:
            - Langsung tulis itinerary, TANPA intro/basa-basi
            - Format wajib per hari:
            
            HARI 1
            Pagi: [aktivitas] - Rp [biaya]
            Siang: [makan di mana] - Rp [biaya]
            Sore: [aktivitas] - Rp [biaya]
            Malam: [makan di mana] - Rp [biaya]
            Total: Rp [total hari ini]
            
            (ulangi untuk tiap hari)
            
            TOTAL TRIP: Rp [angka]
            TIPS: [maks 2 tips singkat]
            
            - JANGAN tulis asumsi, penjelasan, atau catatan tambahan
            - Langsung mulai dari HARI 1
        """.trimIndent()

        return geminiService.generateContent(
            prompt = prompt,
            systemPrompt = systemPrompt
        )
    }
}
