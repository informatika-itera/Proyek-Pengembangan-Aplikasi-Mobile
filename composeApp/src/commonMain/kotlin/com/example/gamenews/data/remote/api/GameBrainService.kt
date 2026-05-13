package com.example.gamenews.data.remote.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class GameBrainResponse(
    val results: List<GameRemoteEntity>,
    @SerialName("total_results") val totalResults: Int,
    val limit: Int,
    val offset: Int
)

@Serializable
data class GameRemoteEntity(
    val id: Int,
    val name: String,
    val year: Double? = null,
    val genre: String? = null,
    val image: String? = null,
    val rating: RatingDTO? = null,
    @SerialName("short_description") val shortDescription: String? = null,
    val developer: String? = null,
    val screenshots: List<String>? = emptyList(),
    val gameplay: String? = null
)

@Serializable
data class RatingDTO(
    val mean: Double,
    val count: Double
)

class GameBrainService(private val httpClient: HttpClient) {
    private val baseUrl = "https://api.gamebrain.co/v1"
    private val apiKey = "8b826ecb4cb14ea6a1dcb8b0fe3c37f3"

    suspend fun searchGames(
        query: String = "*",
        releaseDate: String? = null,
        sortBy: String? = "release_date",
        platform: String? = null,
        genre: String? = null
    ): Result<GameBrainResponse> {
        return try {
            val response = httpClient.get("$baseUrl/games") {
                header(HttpHeaders.Authorization, "Bearer $apiKey")
                parameter("query", query)
                parameter("sort", sortBy)
                parameter("limit", "20")
                if (!sortBy.isNullOrBlank()) parameter("sort", sortBy)
                if (!platform.isNullOrBlank()) parameter("platform", platform)
                if (!genre.isNullOrBlank()) parameter("genre", genre)
                if (!releaseDate.isNullOrBlank()) {
                    parameter("release_date", releaseDate)
                }
            }

            if (response.status == HttpStatusCode.OK) {
                Result.success(response.body<GameBrainResponse>())
            } else {
                Result.failure(Exception("HTTP Error: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGameDetails(gameId: Long): Result<GameRemoteEntity> {
        return try {
            val response = httpClient.get("$baseUrl/games/$gameId") {
                header(HttpHeaders.Authorization, "Bearer $apiKey")
            }
            if (response.status == HttpStatusCode.OK) {
                Result.success(response.body<GameRemoteEntity>())
            } else {
                Result.failure(Exception("HTTP Error: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}