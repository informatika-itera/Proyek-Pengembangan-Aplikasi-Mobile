package com.studymate.domain.repository

import com.studymate.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNotes(): Flow<List<Note>>
    fun getNoteById(id: Long): Flow<Note?>
    fun getNotesBySubject(subject: String): Flow<List<Note>>
    suspend fun insertNote(note: Note): Long
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(id: Long)
}
