package com.example.Feelia.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.Feelia.data.local.NoteDatabase
import com.example.Feelia.data.local.entity.toDomain
import com.example.Feelia.data.local.entity.toDomainList
import com.example.Feelia.data.local.entity.toEntityValues
import com.example.Feelia.domain.model.Emotion
import com.example.Feelia.domain.model.Note
import com.example.Feelia.domain.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

// sudah disesuaikan isi dari getNotesByEmotion(), getPinnedNotes() dll
class NoteRepositoryImpl(private val database: NoteDatabase) : NoteRepository {

    private val queries = database.noteQueries

    override fun getAllNotes(): Flow<List<Note>> {
        return queries.getAllNotes()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { it.toDomainList() }
    }

    override fun getPinnedNotes(): Flow<List<Note>> {
        return queries.getPinnedNotes()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { it.toDomainList() }
    }

    override fun getNotesByEmotion(emotion: Emotion): Flow<List<Note>> {
        return queries.getNotesByEmotion(emotion.name)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { it.toDomainList() }
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        return queries.searchNotes(query)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { it.toDomainList() }
    }

    override fun getNoteById(id: Long): Flow<Note?> {
        return queries.getNoteById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { it?.toDomain() }
    }

    override suspend fun insertNote(note: Note): Long = withContext(Dispatchers.Default) {
        val v = note.toEntityValues()
        queries.insertNote(
            content = v.content,
            emotion = v.emotion,
            is_pinned = v.isPinned,
            created_at = v.createdAt,
            updated_at = v.updatedAt
        )
        queries.lastInsertId().executeAsOne()
    }

    override suspend fun updateNote(note: Note) = withContext(Dispatchers.Default) {
        val v = note.toEntityValues()
        queries.updateNote(
            id = note.id,
            content = v.content,
            emotion = v.emotion,
            is_pinned = v.isPinned,
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
}