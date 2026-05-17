package com.studymate.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.studymate.data.local.NoteEntity
import com.studymate.data.local.StudyMateDatabase
import com.studymate.domain.model.Note
import com.studymate.domain.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class NoteRepositoryImpl(
    private val database: StudyMateDatabase
) : NoteRepository {
    private val queries = database.noteQueries

    override fun getAllNotes(): Flow<List<Note>> {
        return queries.selectAllNotes()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toDomain() } }
    }

    override fun getNoteById(id: Long): Flow<Note?> {
        return queries.selectNoteById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toDomain() }
    }

    override fun getNotesBySubject(subject: String): Flow<List<Note>> {
        return queries.selectNotesBySubject(subject)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toDomain() } }
    }

    override fun insertNote(note: Note): Long {
        queries.insertNote(
            title = note.title,
            rawContent = note.rawContent,
            refinedContent = note.refinedContent,
            subject = note.subject,
            isRefined = if (note.isRefined) 1L else 0L,
            createdAt = note.createdAt,
            updatedAt = note.updatedAt
        )
        return queries.lastInsertRowId().executeAsOne()
    }

    override suspend fun updateNote(note: Note) {
        withContext(Dispatchers.IO) {
            queries.updateNote(
                title = note.title,
                refinedContent = note.refinedContent,
                isRefined = if (note.isRefined) 1L else 0L,
                updatedAt = note.updatedAt,
                id = note.id
            )
        }
    }

    override suspend fun deleteNote(id: Long) {
        withContext(Dispatchers.IO) {
            queries.deleteNote(id)
        }
    }

    private fun NoteEntity.toDomain(): Note {
        return Note(
            id = id,
            title = title,
            rawContent = rawContent,
            refinedContent = refinedContent,
            subject = subject,
            isRefined = isRefined == 1L,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
