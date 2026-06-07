package com.soundletter.app.domain.repository

import com.soundletter.app.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface LetterRepository {
    /**
     * Riwayat Lokal: Data dari database lokal (SQLDelight)
     */
    fun getLetters(): Flow<List<Note>>
    
    /**
     * Feed Global: Data statis dummy (Pengganti Supabase)
     */
    fun getGlobalLetters(): Flow<List<Note>>

    /**
     * Pencarian Hybrid: Mencari dari Database Lokal dan Data Dummy Global
     */
    fun searchLetters(query: String): Flow<List<Note>>

    suspend fun getLetterById(id: Long): Note?
    
    /**
     * Mengirim pesan: Hanya disimpan di lokal (Offline-only mode)
     */
    suspend fun sendLetter(letter: Note): Boolean

    suspend fun deleteLetter(id: Long)

    /**
     * Menghapus semua riwayat pesan lokal
     */
    suspend fun clearHistory()
}
