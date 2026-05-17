package com.example.mybawanggacha.data.remote.jikan.api

import com.example.mybawanggacha.data.remote.jikan.dto.AnimeDetailResponse
import com.example.mybawanggacha.data.remote.jikan.dto.AnimeEpisodesResponse
import com.example.mybawanggacha.data.remote.jikan.dto.JikanAnimeListResponse
import com.example.mybawanggacha.data.remote.jikan.dto.JikanRecommendationsResponse
import com.example.mybawanggacha.data.remote.jikan.dto.JikanSeasonArchiveResponse
import com.example.mybawanggacha.data.remote.jikan.dto.RelationEntryPreviewResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class JikanService(private val client: HttpClient) {
    companion object {
        private const val BASE_URL = "https://api.jikan.moe/v4/"
    }

    suspend fun fetchAnimeRecommendations(): JikanRecommendationsResponse {
        return client.get("${BASE_URL}recommendations/anime").body()
    }

    suspend fun fetchCurrentSeasonAnime(page: Int = 1): JikanAnimeListResponse {
        return client.get("${BASE_URL}seasons/now") {
            parameter("page", page)
        }.body()
    }

    suspend fun fetchSeasonAnime(
        year: Int,
        season: String,
        page: Int = 1
    ): JikanAnimeListResponse {
        return client.get("${BASE_URL}seasons/$year/$season") {
            parameter("page", page)
        }.body()
    }

    suspend fun fetchUpcomingSeasonAnime(page: Int = 1): JikanAnimeListResponse {
        return client.get("${BASE_URL}seasons/upcoming") {
            parameter("page", page)
        }.body()
    }

    suspend fun fetchTopAnime(page: Int = 1): JikanAnimeListResponse {
        return client.get("${BASE_URL}top/anime") {
            parameter("page", page)
        }.body()
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