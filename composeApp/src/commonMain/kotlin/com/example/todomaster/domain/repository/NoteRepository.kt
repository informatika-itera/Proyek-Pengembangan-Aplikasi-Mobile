package com.example.todomaster.domain.repository

import com.example.todomaster.domain.model.Note
import com.example.todomaster.domain.model.NoteCategory
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNotes(): Flow<List<Note>>
    fun getPinnedNotes(): Flow<List<Note>>
    fun getNotesByCategory(category: NoteCategory): Flow<List<Note>>
    fun searchNotes(query: String): Flow<List<Note>>
    fun getNoteById(id: Long): Flow<Note?>
    suspend fun insertNote(note: Note): Long
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(id: Long)
    suspend fun togglePinNote(id: Long)
    suspend fun deleteNotes(ids: List<Long>)
}
