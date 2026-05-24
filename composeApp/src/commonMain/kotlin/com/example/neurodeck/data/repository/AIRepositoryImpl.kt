package com.example.neurodeck.data.repository

import com.example.neurodeck.data.remote.api.GeminiService
import com.example.neurodeck.domain.repository.AIRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/**
 * SQLDelight-independent implementation of [AIRepository] backed by [GeminiService].
 *
 * Mapping: GeneratedFlashcardDto → Pair<String, String> (front, back).
 * Use Pair (bukan custom data class) karena ini hanya intermediate data
 * antara API call dan UI preview. Tidak ada lifecycle, tidak ada persistence.
 */
class AIRepositoryImpl(
    private val geminiService: GeminiService,
) : AIRepository {

    override suspend fun generateFlashcards(material: String): List<Pair<String, String>> =
        withContext(Dispatchers.Default) {
            // Validasi minimal di repository layer
            require(material.isNotBlank()) { "Materi tidak boleh kosong" }
            require(material.length <= 30_000) {
                "Materi terlalu panjang (max 30.000 karakter). Bagi materi jadi beberapa chunk."
            }

            geminiService
                .generateFlashcards(material.trim())
                .map { dto -> dto.front to dto.back }
        }

    override suspend fun chatWithHistory(history: List<Pair<String, String>>): String =
        withContext(Dispatchers.Default) {
            require(history.isNotEmpty()) { "History tidak boleh kosong" }
            // Last message should be from user (else AI tidak punya pertanyaan untuk dijawab)
            require(history.last().first == "user") {
                "Last message in history harus dari 'user' (current question)"
            }
            geminiService.chatWithHistory(history)
        }
}