package com.example.fitkos.data.repository

import com.example.fitkos.domain.model.Note
import com.example.fitkos.domain.model.NoteCategory
import com.example.fitkos.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeNoteRepository : NoteRepository {
    
    private val notes = MutableStateFlow<List<Note>>(emptyList())
    private var nextId = 1L
    
    override fun getAllNotes(): Flow<List<Note>> = notes
    
    override fun getPinnedNotes(): Flow<List<Note>> {
        return notes.map { list -> list.filter { it.isPinned } }
    }
    
    override fun getNotesByCategory(category: NoteCategory): Flow<List<Note>> {
        return notes.map { list -> list.filter { it.category == category } }
    }
    
    override fun searchNotes(query: String): Flow<List<Note>> {
        return notes.map { list ->
            list.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.content.contains(query, ignoreCase = true)
            }
        }
    }
    
    override fun getNoteById(id: Long): Flow<Note?> {
        return notes.map { list -> list.find { it.id == id } }
    }
    
    override suspend fun insertNote(note: Note): Long {
        val id = nextId++
        val newNote = note.copy(id = id)
        notes.update { it + newNote }
        return id
    }
    
    override suspend fun updateNote(note: Note) {
        notes.update { list ->
            list.map { if (it.id == note.id) note else it }
        }
    }
    
    override suspend fun deleteNote(id: Long) {
        notes.update { list -> list.filter { it.id != id } }
    }
    
    override suspend fun togglePinNote(id: Long) {
        notes.update { list ->
            list.map { 
                if (it.id == id) it.copy(isPinned = !it.isPinned) else it 
            }
        }
    }
    
    override suspend fun deleteNotes(ids: List<Long>) {
        notes.update { list -> list.filter { it.id !in ids } }
    }
}
