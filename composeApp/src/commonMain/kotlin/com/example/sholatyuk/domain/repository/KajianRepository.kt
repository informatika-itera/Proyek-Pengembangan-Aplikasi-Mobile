package com.example.sholatyuk.domain.repository

import com.example.sholatyuk.domain.model.KajianNote
import com.example.sholatyuk.domain.model.KajianCategory
import kotlinx.coroutines.flow.Flow

interface KajianRepository {
    fun getAllNotes(): Flow<List<KajianNote>>
    fun getNotesByCategory(category: KajianCategory): Flow<List<KajianNote>>
    fun searchNotes(query: String): Flow<List<KajianNote>>
    suspend fun insertNote(note: KajianNote)
    suspend fun deleteNote(id: String)
}