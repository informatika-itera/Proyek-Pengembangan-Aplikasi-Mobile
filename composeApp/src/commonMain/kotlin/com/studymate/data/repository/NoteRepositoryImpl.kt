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
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class NoteRepositoryImpl(
    private val database: StudyMateDatabase
) : NoteRepository {

    override fun getAllNotes(): Flow<List<Note>> {
        return database.noteQueries.selectAllNotes()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toDomain() } }
    }

    override fun getNoteById(id: Long): Flow<Note?> {
        return database.noteQueries.selectNoteById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toDomain() }
    }

    override fun getNotesBySubject(subject: String): Flow<List<Note>> {
        return database.noteQueries.selectNotesBySubject(subject)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun insertNote(note: Note): Long {
        return withContext(Dispatchers.IO) {
            database.noteQueries.insertNote(
                title = note.title,
                rawContent = note.rawContent,
                refinedContent = note.refinedContent,
                subject = note.subject,
                isRefined = if (note.isRefined) 1L else 0L,
                createdAt = note.createdAt,
                updatedAt = note.updatedAt
            )
            val noteId = database.noteQueries.lastInsertRowId().executeAsOne()
            
            // Record activity
            val today = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date.toString()
            database.activityQueries.incrementNotesCount(today)
            
            noteId
        }
    }

    override suspend fun updateNote(note: Note) {
        withContext(Dispatchers.IO) {
            database.noteQueries.updateNote(
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
            database.noteQueries.deleteNote(id)
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
