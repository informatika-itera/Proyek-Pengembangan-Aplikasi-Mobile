package com.soundletter.app.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.soundletter.app.data.local.SoundLetterDatabase
import com.soundletter.app.data.local.entity.toDomain
import com.soundletter.app.domain.model.Note
import com.soundletter.app.domain.repository.LetterRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.*
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseLetterDto(
    val recipient: String,
    val sender: String,
    val content: String,
    val song_title: String? = null,
    val song_artist: String? = null
)

class LetterRepositoryImpl(
    private val database: SoundLetterDatabase,
    private val supabase: SupabaseClient
) : LetterRepository {
    
    private val queries = database.noteQueries

    override fun getGlobalLetters(): Flow<List<Note>> = flow {
        try {
            val response = supabase.postgrest.from("letters")
                .select {
                    order("created_at", order = Order.DESCENDING)
                }
            
            val notes = response.decodeList<SupabaseLetterDto>().map { dto ->
                Note(
                    recipient = dto.recipient,
                    sender = dto.sender,
                    content = dto.content,
                    songTitle = dto.song_title,
                    songArtist = dto.song_artist
                )
            }
            emit(notes)
        } catch (e: Exception) {
            println("Supabase Global Error: ${e.message}")
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)

    override fun getLetters(): Flow<List<Note>> {
        return queries.getAllNotes()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun sendLetter(letter: Note): Boolean {
        var isSynced = false
        // 1. Sinkronisasi ke Supabase
        try {
            val dto = SupabaseLetterDto(
                recipient = letter.recipient,
                sender = letter.sender,
                content = letter.content,
                song_title = letter.songTitle,
                song_artist = letter.songArtist
            )
            supabase.postgrest.from("letters").insert(dto)
            isSynced = true
        } catch (e: Exception) {
            println("Offline Mode: Sync failed. ${e.message}")
            isSynced = false
        }

        // 2. Simpan lokal
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
        return isSynced
    }

    override suspend fun getLetterById(id: Long): Note? {
        return queries.getNoteById(id).executeAsOneOrNull()?.toDomain()
    }

    override suspend fun deleteLetter(id: Long) {
        queries.deleteNoteById(id)
    }

    override suspend fun clearHistory() {
        queries.deleteAllNotes()
    }
}
