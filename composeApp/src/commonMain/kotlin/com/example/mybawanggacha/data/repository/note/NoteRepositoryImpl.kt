package com.example.mybawanggacha.data.repository.note

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.mybawanggacha.core.coroutines.AppDispatchers
import com.example.mybawanggacha.data.local.NoteDatabase
import com.example.mybawanggacha.data.local.entity.toDomain
import com.example.mybawanggacha.data.local.entity.toDomainList
import com.example.mybawanggacha.data.local.entity.toEntityValues
import com.example.mybawanggacha.domain.note.model.Note
import com.example.mybawanggacha.domain.note.model.NoteCategory
import com.example.mybawanggacha.domain.note.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.time.Clock

class NoteRepositoryImpl(
    private val database: NoteDatabase,
    private val dispatchers: AppDispatchers
) : NoteRepository {

    private val queries = database.noteQueries

    override fun getAllNotes(): Flow<List<Note>> {
        return queries.getAllNotes()
            .asFlow()
            .mapToList(dispatchers.io)
            .map { entities -> entities.toDomainList() }
    }

    override fun getPinnedNotes(): Flow<List<Note>> {
        return queries.getPinnedNotes()
            .asFlow()
            .mapToList(dispatchers.io)
            .map { entities -> entities.toDomainList() }
    }

    override fun getNotesByCategory(category: NoteCategory): Flow<List<Note>> {
        return queries.getNotesByCategory(category.name)
            .asFlow()
            .mapToList(dispatchers.io)
            .map { entities -> entities.toDomainList() }
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        return queries.searchNotes(query, query)
            .asFlow()
            .mapToList(dispatchers.io)
            .map { entities -> entities.toDomainList() }
    }

    override fun getNoteById(id: Long): Flow<Note?> {
        return queries.getNoteById(id)
            .asFlow()
            .mapToOneOrNull(dispatchers.io)
            .map { entity -> entity?.toDomain() }
    }

    override suspend fun insertNote(note: Note): Long = withContext(dispatchers.io) {
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

    override suspend fun updateNote(note: Note) {
        withContext(dispatchers.io) {
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
    }

    override suspend fun deleteNote(id: Long) {
        withContext(dispatchers.io) {
            queries.deleteNoteById(id)
        }
    }

    override suspend fun togglePinNote(id: Long) {
        withContext(dispatchers.io) {
            queries.togglePin(
                id = id,
                updated_at = Clock.System.now().toEpochMilliseconds()
            )
        }
    }

    override suspend fun deleteNotes(ids: List<Long>) {
        withContext(dispatchers.io) {
            queries.deleteNotesByIds(ids)
        }
    }
}