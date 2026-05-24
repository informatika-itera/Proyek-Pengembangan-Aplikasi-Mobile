package com.example.neurodeck.domain.model

import kotlinx.datetime.Instant

/**
 * Satu pesan dalam riwayat AI Chat (multi-turn conversation).
 *
 * Disimpan ke tabel ChatMessageEntity, di-load via ChatRepository.observeMessages().
 * Dipakai juga untuk build context history saat call Gemini API
 * (chat dengan history = multi-turn).
 *
 * @property id         Auto-generated dari SQLite. 0 untuk message yang belum di-save.
 * @property role       User atau Assistant — pakai enum supaya exhaustive when di UI bubble rendering.
 * @property content    Plain text atau markdown (AI Tutor prompt encourage markdown).
 * @property timestamp  Epoch millis. Untuk display "2 menit lalu" + ordering.
 * @property isError    True kalau message dari assistant gagal (network/API error).
 *                      UI bisa render bubble dengan tint merah + tombol "Coba lagi".
 */
data class ChatMessage(
    val id: Long = 0,
    val role: MessageRole,
    val content: String,
    val timestamp: Instant,
    val isError: Boolean = false,
)

/**
 * Role di chat conversation. 2 nilai sesuai Gemini API convention:
 *   - User      → user message (pertanyaan)
 *   - Assistant → AI response
 *
 * Disimpan di DB sebagai STRING (bukan enum ordinal) supaya readable di
 * DB browser dan robust ke reorder enum di future.
 */
enum class MessageRole {
    User,
    Assistant;

    companion object {
        /** Safe parsing dari string DB. Fallback ke User untuk row corrupt. */
        fun fromString(value: String): MessageRole =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: User
    }
}