package com.example.Feelia.domain.repository

import com.example.Feelia.domain.model.Emotion
import com.example.Feelia.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNotes(): Flow<List<Note>>
    fun getPinnedNotes(): Flow<List<Note>>
    fun getNotesByEmotion(emotion: Emotion): Flow<List<Note>>
    fun searchNotes(query: String): Flow<List<Note>>
    fun getNoteById(id: Long): Flow<Note?>
    suspend fun insertNote(note: Note): Long
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(id: Long)
    suspend fun togglePinNote(id: Long)
}