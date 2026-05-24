package com.example.neurodeck.domain.repository

import com.example.neurodeck.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

/**
 * Kontrak untuk persist & observe chat history dengan AI Tutor.
 *
 * Backing: SQLDelight (ChatMessageEntity table). Pakai Flow supaya UI
 * auto-update saat pesan baru di-insert (user typing → assistant reply).
 *
 * Method `sendMessage` adalah composite operation:
 *   1. Save user message ke DB
 *   2. Call AIRepository.chatWithHistory(allHistory + new user msg)
 *   3. Save assistant response ke DB (atau error message kalau gagal)
 *
 * Atomicity: TIDAK pakai transaction. Kalau step 2 gagal, step 1 sudah save
 * (user msg masih ada di history). Step 3 simpan error message dengan
 * isError=true supaya user bisa retry. Pattern WhatsApp/Telegram error UX.
 */
interface ChatRepository {

    /** Stream semua chat history, ordered by timestamp ASC (oldest first). */
    fun observeMessages(): Flow<List<ChatMessage>>

    /**
     * Kirim user message dan tunggu reply. Composite operation (lihat KDoc class).
     *
     * @param userMessage Teks pesan dari user.
     * @return Result.Success kalau reply berhasil tersave.
     *         Result.Failure dengan throwable kalau ada error di AI call.
     */
    suspend fun sendMessage(userMessage: String): Result<Unit>

    /** Clear all history. Dipanggil dari "Clear chat" action di TopBar. */
    suspend fun clearHistory()
}