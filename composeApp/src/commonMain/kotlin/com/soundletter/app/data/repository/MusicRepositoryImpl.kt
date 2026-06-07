package com.soundletter.app.data.repository

import com.soundletter.app.core.network.ApiConfig
import com.soundletter.app.domain.model.MusicTrack
import com.soundletter.app.domain.repository.MusicRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JamendoResponse(
    val results: List<JamendoTrack>
)

@Serializable
data class JamendoTrack(
    val name: String,
    @SerialName("artist_name") val artistName: String,
    val image: String,
    val audio: String
)

class MusicRepositoryImpl(
    private val httpClient: HttpClient
) : MusicRepository {

    override suspend fun searchSongs(query: String): List<MusicTrack> {
        return try {
            println("JAMENDO_LOG: Searching for '$query'...")
            
            val response: JamendoResponse = httpClient.get("https://api.jamendo.com/v3.0/tracks/") {
                // Menggunakan ApiConfig sebagai jembatan tunggal ke BuildKonfig
                parameter("client_id", ApiConfig.jamendoClientId)
                parameter("format", "json")
                parameter("limit", "10")
                parameter("search", query)
            }.body()

            if (response.results.isEmpty()) {
                throw Exception("Track not found")
            }

            response.results.map { track ->
                MusicTrack(
                    title = track.name,
                    artist = track.artistName,
                    previewUrl = track.audio,
                    albumArtUrl = track.image
                )
            }
        } catch (e: Exception) {
            println("JAMENDO_LOG: Error: ${e.message}")
            // FALLBACK: Jaminan data selalu muncul meski API mati
            listOf(
                MusicTrack(
                    title = "Creative Commons Melody",
                    artist = "Jamendo Artist (Fallback)",
                    previewUrl = "https://prod-1.storage.jamendo.com/download/track/1885566/mp32/",
                    albumArtUrl = "https://picsum.photos/seed/music/300/300"
                )
            )
        }
    }
}
