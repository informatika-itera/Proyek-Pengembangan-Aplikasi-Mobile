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

    // Data dummy sebagai awalan
    private val dummyNotes = listOf(
        Note(
            id = -1, // ID negatif agar tidak bentrok dengan database
            recipient = "Dzaky",
            sender = "Anonim",
            content = "Semangat buat ngerjain tugasnya!",
            songTitle = "Hati-Hati di Jalan",
            songArtist = "Tulus"
        ),
        Note(
            id = -2,
            recipient = "Gian",
            sender = "Anonim",
            content = "Selamat ulang tahun! Semoga harimu menyenangkan.",
            songTitle = "Happy Birthday",
            songArtist = "Traditional"
        )
    )

    override fun getLetters(): Flow<List<Note>> {
        return queries.getAllNotes()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> 
                // Gabungkan data dummy dengan data dari database
                dummyNotes + entities.map { it.toDomain() }
            }
    }

    override suspend fun getLetterById(id: Long): Note? {
        // Cari di dummy dulu, jika tidak ada baru cari di database
        return dummyNotes.find { it.id == id } 
            ?: queries.getNoteById(id).executeAsOneOrNull()?.toDomain()
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
