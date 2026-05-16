package com.soundletter.app.domain.repository

import com.soundletter.app.domain.model.Note
import com.soundletter.app.domain.model.NoteCategory
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNotes(): Flow<List<Note>>
    fun getPinnedNotes(): Flow<List<Note>>
    fun getNotesByCategory(category: NoteCategory): Flow<List<Note>>
    fun searchNotes(query: String): Flow<List<Note>>
    suspend fun getNoteById(id: Long): Note?
    suspend fun insertNote(note: Note): Long
    suspend fun updateNote(note: Note)
    suspend fun deleteNoteById(id: Long)
    suspend fun togglePin(id: Long, updatedAt: Long)
    suspend fun deleteNotesByIds(ids: List<Long>)
}
