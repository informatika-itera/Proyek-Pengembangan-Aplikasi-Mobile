package com.soundletter.app.data.repository

import com.soundletter.app.domain.repository.MusicRepository
import io.ktor.client.HttpClient

class MusicRepositoryImpl(
    private val httpClient: HttpClient
) : MusicRepository {
    override suspend fun searchSongs(query: String): List<String> {
        // Implementation for Spotify API using Ktor
        return emptyList()
    }
}
