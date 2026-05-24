package com.example.neurodeck.domain.repository

/**
 * Kontrak untuk AI service yang generate flashcards.
 *
 * Berbeda dengan [DeckRepository] dan [CardRepository] yang berbasis Flow + local DB,
 * AIRepository adalah one-shot suspend: panggil → tunggu → dapat hasil (atau error).
 *
 * Implementasi: AIRepositoryImpl (data layer), backed by Gemini API.
 *
 * Catatan: return list of Pair (front, back) — bukan domain model Card.
 * Reason: hasil belum di-persist ke DB. ViewModel yang putuskan apa yang di-save
 * (user bisa hapus card individual sebelum konfirmasi save).
 */
interface AIRepository {

    /**
     * Generate flashcards dari teks materi user.
     *
     * @param material Teks bebas: paste dari slide, catatan, ringkasan, dll.
     * @return List of (front, back) pairs. Bisa 0 item kalau AI tidak menghasilkan apa-apa.
     * @throws Exception dengan message user-friendly kalau gagal (network, rate limit, dll).
     */
    suspend fun generateFlashcards(material: String): List<Pair<String, String>>

    /**
     * Multi-turn chat dengan AI Tutor (P3f).
     *
     * @param history List of (role, content). Role "user" untuk user msg,
     *                "model" untuk AI reply previous turns. Oldest first.
     * @return Plain text reply dari AI (bisa markdown).
     * @throws Exception dengan message user-friendly kalau gagal.
     */
    suspend fun chatWithHistory(history: List<Pair<String, String>>): String
}