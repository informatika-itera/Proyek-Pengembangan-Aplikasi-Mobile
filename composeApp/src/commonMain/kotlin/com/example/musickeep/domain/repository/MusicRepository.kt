package com.example.musickeep.domain.repository

import com.example.musickeep.domain.model.Music
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    fun getAllMusic(): Flow<List<Music>>
    fun searchMusic(query: String): Flow<List<Music>>
    suspend fun getMusicById(id: Long): Music?
    suspend fun insertMusic(music: Music)
    suspend fun updateMusic(music: Music)
    suspend fun deleteMusic(id: Long)
}
