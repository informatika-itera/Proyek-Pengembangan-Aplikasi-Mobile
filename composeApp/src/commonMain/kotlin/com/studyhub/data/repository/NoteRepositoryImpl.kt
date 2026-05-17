package com.studyhub.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.studyhub.data.local.StudyHubDatabase
import com.studyhub.domain.model.Note
import com.studyhub.domain.model.NoteCategory
import com.studyhub.domain.model.NoteColor
import com.studyhub.domain.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class NoteRepositoryImpl(private val database: StudyHubDatabase) : NoteRepository {
    
    private val queries = database.noteQueries
    
    override fun getAllNotes(): Flow<List<Note>> {
        return queries.getAllNotes()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }
    }
    
    override fun getPinnedNotes(): Flow<List<Note>> {
        return queries.getPinnedNotes()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }
    }
    
    override fun getNotesByCategory(category: NoteCategory): Flow<List<Note>> {
        return queries.getNotesByCategory(category.name)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }
    }
    
    override fun searchNotes(query: String): Flow<List<Note>> {
        return queries.searchNotes(query, query)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }
    }
    
    override fun getNoteById(id: Long): Flow<Note?> {
        return queries.getNoteById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { entity -> entity?.toDomain() }
    }
    
    override suspend fun insertNote(note: Note): Long = withContext(Dispatchers.IO) {
        queries.insertNote(
            title = note.title,
            content = note.content,
            category = note.category.name,
            color = note.color.name,
            is_pinned = if (note.isPinned) 1L else 0L,
            created_at = note.createdAt.toEpochMilliseconds(),
            updated_at = note.updatedAt.toEpochMilliseconds()
        )
        queries.lastInsertId().executeAsOne()
    }
    
    override suspend fun updateNote(note: Note) = withContext(Dispatchers.IO) {
        queries.updateNote(
            id = note.id,
            title = note.title,
            content = note.content,
            category = note.category.name,
            color = note.color.name,
            is_pinned = if (note.isPinned) 1L else 0L,
            updated_at = Clock.System.now().toEpochMilliseconds()
        )
    }
    
    override suspend fun deleteNote(id: Long) = withContext(Dispatchers.IO) {
        queries.deleteNoteById(id)
    }
    
    override suspend fun togglePinNote(id: Long) = withContext(Dispatchers.IO) {
        queries.togglePin(
            id = id,
            updated_at = Clock.System.now().toEpochMilliseconds()
        )
    }
    
    override suspend fun deleteNotes(ids: List<Long>) = withContext(Dispatchers.IO) {
        queries.deleteNotesByIds(ids)
    }

    private fun com.studyhub.data.local.NoteEntity.toDomain(): Note {
        return Note(
            id = id,
            title = title,
            content = content,
            category = NoteCategory.fromString(category),
            color = NoteColor.fromString(color),
            isPinned = is_pinned == 1L,
            createdAt = Instant.fromEpochMilliseconds(created_at),
            updatedAt = Instant.fromEpochMilliseconds(updated_at)
        )
    }
}
