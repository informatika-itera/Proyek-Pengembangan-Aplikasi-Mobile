package com.soundletter.app.domain.repository

import com.soundletter.app.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface LetterRepository {
    /**
     * Riwayat Lokal: Data dari database lokal (SQLDelight)
     */
    fun getLetters(): Flow<List<Note>>
    
    /**
     * Feed Global: Data dari Supabase (Cloud)
     */
    fun getGlobalLetters(): Flow<List<Note>>

    suspend fun getLetterById(id: Long): Note?
    
    /**
     * Mengirim pesan ke Supabase (jika online) dan menyimpannya di lokal.
     * @return true jika berhasil sinkron ke cloud, false jika hanya tersimpan di lokal (offline).
     */
    suspend fun sendLetter(letter: Note): Boolean

    suspend fun deleteLetter(id: Long)

    /**
     * Menghapus semua riwayat pesan lokal
     */
    suspend fun clearHistory()
}
