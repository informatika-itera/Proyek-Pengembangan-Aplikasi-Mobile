package com.example.noteai.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.noteai.data.local.NoteDatabase
import com.example.noteai.data.local.entity.toDomain
import com.example.noteai.data.local.entity.toDomainList
import com.example.noteai.data.local.entity.toEntityValues
import com.example.noteai.domain.model.Note
import com.example.noteai.domain.model.NoteCategory
import com.example.noteai.domain.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class NoteRepositoryImpl(private val database: NoteDatabase) : NoteRepository {
    
    private val queries = database.noteQueries
    
    override fun getAllNotes(): Flow<List<Note>> {
        return queries.getAllNotes()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.toDomainList() }
    }
    
    override fun getPinnedNotes(): Flow<List<Note>> {
        return queries.getPinnedNotes()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.toDomainList() }
    }
    
    override fun getNotesByCategory(category: NoteCategory): Flow<List<Note>> {
        return queries.getNotesByCategory(category.name)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.toDomainList() }
    }
    
    override fun searchNotes(query: String): Flow<List<Note>> {
        return queries.searchNotes(query, query)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.toDomainList() }
    }
    
    override fun getNoteById(id: Long): Flow<Note?> {
        return queries.getNoteById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { entity -> entity?.toDomain() }
    }
    
    override suspend fun insertNote(note: Note): Long = withContext(Dispatchers.Default) {
        val values = note.toEntityValues()
        queries.insertNote(
            title = values.title,
            content = values.content,
            category = values.category,
            color = values.color,
            is_pinned = values.isPinned,
            created_at = values.createdAt,
            updated_at = values.updatedAt
        )
        queries.lastInsertId().executeAsOne()
    }
    
    override suspend fun updateNote(note: Note) = withContext(Dispatchers.Default) {
        val values = note.toEntityValues()
        queries.updateNote(
            id = note.id,
            title = values.title,
            content = values.content,
            category = values.category,
            color = values.color,
            is_pinned = values.isPinned,
            updated_at = Clock.System.now().toEpochMilliseconds()
        )
    }
    
    override suspend fun deleteNote(id: Long) = withContext(Dispatchers.Default) {
        queries.deleteNoteById(id)
    }
    
    override suspend fun togglePinNote(id: Long) = withContext(Dispatchers.Default) {
        queries.togglePin(
            id = id,
            updated_at = Clock.System.now().toEpochMilliseconds()
        )
    }
    
    override suspend fun deleteNotes(ids: List<Long>) = withContext(Dispatchers.Default) {
        queries.deleteNotesByIds(ids)
    }
}
