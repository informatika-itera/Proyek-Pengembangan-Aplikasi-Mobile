package com.soundletter.app.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.soundletter.app.data.local.SoundLetterDatabase
import com.soundletter.app.data.local.entity.toDomain
import com.soundletter.app.domain.model.Note
import com.soundletter.app.domain.repository.LetterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LetterRepositoryImpl(
    private val database: SoundLetterDatabase
) : LetterRepository {
    
    private val queries = database.noteQueries

    override fun getLetters(): Flow<List<Note>> {
        return queries.getAllNotes()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getLetterById(id: Long): Note? {
        return queries.getNoteById(id).executeAsOneOrNull()?.toDomain()
    }

    override suspend fun sendLetter(letter: Note) {
        // 1. Simpan ke Lokal (History)
        queries.insertNote(
            recipient = letter.recipient,
            sender = letter.sender,
            content = letter.content,
            song_title = letter.songTitle,
            song_artist = letter.songArtist,
            category = letter.category.name,
            color = letter.color.name,
            is_pinned = if (letter.isPinned) 1L else 0L,
            created_at = letter.createdAt.toEpochMilliseconds(),
            updated_at = letter.updatedAt.toEpochMilliseconds()
        )

        // 2. TODO: Simpan ke Firebase Firestore (Remote)
        // val firestore = Firebase.firestore
        // firestore.collection("letters").add(letter)
    }

    override suspend fun deleteLetter(id: Long) {
        queries.deleteNoteById(id)
    }
}
