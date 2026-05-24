package com.example.noteai.domain.repository

import com.example.noteai.domain.model.Note
import com.example.noteai.domain.model.VulnSeverity
import com.example.noteai.domain.model.VulnStatus
import com.example.noteai.domain.model.VulnType
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNotes(): Flow<List<Note>>
    fun getPinnedNotes(): Flow<List<Note>>
    fun getNotesBySeverity(severity: VulnSeverity): Flow<List<Note>>
    fun getNotesByStatus(status: VulnStatus): Flow<List<Note>>
    fun getNotesByVulnType(vulnType: VulnType): Flow<List<Note>>
    fun searchNotes(query: String): Flow<List<Note>>
    fun getNoteById(id: Long): Flow<Note?>
    suspend fun insertNote(note: Note): Long
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(id: Long)
    suspend fun togglePinNote(id: Long)
    suspend fun deleteNotes(ids: List<Long>)
}
