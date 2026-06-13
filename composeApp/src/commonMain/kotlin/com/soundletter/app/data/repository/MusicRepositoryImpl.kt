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

    override suspend fun searchSongs(mood: String): List<MusicTrack> {
        return try {
            val formattedTags = mood.trim().replace(" ", "+")
            
            val response: JamendoResponse = httpClient.get("https://api.jamendo.com/v3.0/tracks/") {
                parameter("client_id", ApiConfig.jamendoClientId)
                parameter("format", "json")
                parameter("limit", "10")
                parameter("fuzzytags", formattedTags)
                parameter("boost", "popularity_month")
            }.body()

            if (response.results.isEmpty()) throw Exception("No tracks found")

            response.results.map { track ->
                MusicTrack(
                    title = track.name,
                    artist = track.artistName,
                    previewUrl = track.audio,
                    albumArtUrl = track.image
                )
            }
        } catch (e: Exception) {
            // Fallback dengan lagu Jamendo yang valid jika API error
            listOf(
                MusicTrack(
                    title = "Ambient Gold",
                    artist = "AudioCoffee",
                    previewUrl = "https://www.jamendo.com/track/1885903/get/stream",
                    albumArtUrl = "https://picsum.photos/seed/music/300/300"
                )
            )
        }
    }
}
