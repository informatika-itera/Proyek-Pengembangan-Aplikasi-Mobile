package com.example.musickeep.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.musickeep.data.local.MusicDatabase
import com.example.musickeep.data.mapper.toDomain
import com.example.musickeep.domain.model.Music
import com.example.musickeep.domain.repository.MusicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MusicRepositoryImpl(
    db: MusicDatabase
) : MusicRepository {
    private val queries = db.musicQueries

    override fun getAllMusic(): Flow<List<Music>> {
        return queries.getAllMusic()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun searchMusic(query: String): Flow<List<Music>> {
        return queries.searchMusic(query, query)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getMusicById(id: Long): Music? {
        return queries.getMusicById(id).executeAsOneOrNull()?.toDomain()
    }

    override suspend fun insertMusic(music: Music) {
        queries.insertMusic(
            title = music.title,
            artist = music.artist,
            genre = music.genre,
            mood = music.mood,
            created_at = music.createdAt,
            updated_at = music.updatedAt
        )
    }

    override suspend fun updateMusic(music: Music) {
        queries.updateMusic(
            title = music.title,
            artist = music.artist,
            genre = music.genre,
            mood = music.mood,
            updated_at = music.updatedAt,
            id = music.id ?: 0L
        )
    }

    override suspend fun deleteMusic(id: Long) {
        queries.deleteMusicById(id)
    }
}
