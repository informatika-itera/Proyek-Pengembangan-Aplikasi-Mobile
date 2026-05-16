package com.example.mybawanggacha.data.remote.api

import com.example.mybawanggacha.data.remote.dto.AnimeDetailResponse
import com.example.mybawanggacha.data.remote.dto.AnimeEpisodesResponse
import com.example.mybawanggacha.data.remote.dto.JikanAnimeListResponse
import com.example.mybawanggacha.data.remote.dto.JikanSeasonArchiveResponse
import com.example.mybawanggacha.data.remote.dto.JikanRecommendationsResponse
import com.example.mybawanggacha.data.remote.dto.RelationEntryPreviewResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class JikanService(private val client: HttpClient) {
    companion object {
        private const val BASE_URL = "https://api.jikan.moe/v4/"
    }

    suspend fun fetchAnimeRecommendations(): JikanRecommendationsResponse {
        return client.get("${BASE_URL}recommendations/anime").body()
    }

    suspend fun fetchCurrentSeasonAnime(): JikanAnimeListResponse {
        return client.get("${BASE_URL}seasons/now").body()
    }

    suspend fun fetchSeasonAnime(year: Int, season: String): JikanAnimeListResponse {
        return client.get("${BASE_URL}seasons/$year/$season").body()
    }

    suspend fun fetchUpcomingSeasonAnime(): JikanAnimeListResponse {
        return client.get("${BASE_URL}seasons/upcoming").body()
    }

    suspend fun fetchTopAnime(): JikanAnimeListResponse {
        return client.get("${BASE_URL}top/anime").body()
    }

    suspend fun fetchSeasonArchive(): JikanSeasonArchiveResponse {
        return client.get("${BASE_URL}seasons").body()
    }

    suspend fun fetchAnimeDetail(id: Int): AnimeDetailResponse {
        return client.get("${BASE_URL}anime/$id").body()
    }

    suspend fun fetchAnimeFullDetail(id: Int): AnimeDetailResponse {
        return client.get("${BASE_URL}anime/$id/full").body()
    }

    suspend fun fetchAnimeEpisodes(id: Int): AnimeEpisodesResponse {
        return client.get("${BASE_URL}anime/$id/episodes").body()
    }

    suspend fun fetchRelationEntryPreview(
        id: Int,
        type: String?
    ): RelationEntryPreviewResponse {
        val resource = when (type?.lowercase()) {
            "manga" -> "manga"
            else -> "anime"
        }

        return client.get("${BASE_URL}$resource/$id").body()
    }
}