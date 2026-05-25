package com.example.mybawanggacha.data.remote.jikan.api

import com.example.mybawanggacha.data.remote.jikan.dto.AnimeDetailResponse
import com.example.mybawanggacha.data.remote.jikan.dto.AnimeEpisodesResponse
import com.example.mybawanggacha.data.remote.jikan.dto.JikanAnimeListResponse
import com.example.mybawanggacha.data.remote.jikan.dto.JikanRecommendationsResponse
import com.example.mybawanggacha.data.remote.jikan.dto.JikanSeasonArchiveResponse
import com.example.mybawanggacha.data.remote.jikan.dto.MangaDetailResponse
import com.example.mybawanggacha.data.remote.jikan.dto.RelationEntryPreviewResponse
import com.example.mybawanggacha.data.remote.jikan.dto.WatchEpisodesResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import com.example.mybawanggacha.domain.search.model.MediaSearchFilters
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class JikanService(private val client: HttpClient) {
    companion object {
        private const val BASE_URL = "https://api.jikan.moe/v4/"
    }

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun fetchAnimeRecommendations(): JikanRecommendationsResponse {
        return getBody("recommendations/anime")
    }

    suspend fun fetchMangaRecommendations(): JikanRecommendationsResponse {
        return getBody("recommendations/manga")
    }

    suspend fun fetchRandomAnime(): AnimeDetailResponse {
        return getBody("random/anime")
    }

    suspend fun fetchRandomManga(): MangaDetailResponse {
        return getBody("random/manga")
    }

    suspend fun fetchRecentWatchEpisodes(page: Int = 1): WatchEpisodesResponse {
        return getBody("watch/episodes") {
            parameter("page", page)
        }
    }

    suspend fun fetchAnimeSearch(
        filters: MediaSearchFilters,
        page: Int = 1
    ): JikanAnimeListResponse {
        return getBody("anime") {
            applyCommonSearchParameters(filters = filters, page = page)
            parameterIfNotBlank("type", filters.type)
            parameterIfNotBlank("status", filters.status)
            parameterIfNotBlank("rating", filters.rating)
            parameterIfNotBlank("producers", filters.producers)
        }
    }

    suspend fun fetchMangaSearch(
        filters: MediaSearchFilters,
        page: Int = 1
    ): JikanAnimeListResponse {
        return getBody("manga") {
            applyCommonSearchParameters(filters = filters, page = page)
            parameterIfNotBlank("type", filters.type)
            parameterIfNotBlank("status", filters.status)
            parameterIfNotBlank("magazines", filters.magazines)
        }
    }

    suspend fun fetchCurrentSeasonAnime(page: Int = 1): JikanAnimeListResponse {
        return getBody("seasons/now") {
            parameter("page", page)
        }
    }

    suspend fun fetchSeasonAnime(
        year: Int,
        season: String,
        page: Int = 1
    ): JikanAnimeListResponse {
        return getBody("seasons/$year/$season") {
            parameter("page", page)
        }
    }

    suspend fun fetchUpcomingSeasonAnime(page: Int = 1): JikanAnimeListResponse {
        return getBody("seasons/upcoming") {
            parameter("page", page)
        }
    }

    suspend fun fetchTopAnime(page: Int = 1): JikanAnimeListResponse {
        return getBody("top/anime") {
            parameter("page", page)
        }
    }

    suspend fun fetchTopManga(
        page: Int = 1,
        type: String? = null,
        filter: String? = null
    ): JikanAnimeListResponse {
        return getBody("top/manga") {
            parameter("page", page)
            type?.let { parameter("type", it) }
            filter?.let { parameter("filter", it) }
        }
    }

    suspend fun fetchMangaFullDetail(id: Int): MangaDetailResponse {
        return getBody("manga/$id/full")
    }

    suspend fun fetchSeasonArchive(): JikanSeasonArchiveResponse {
        return getBody("seasons")
    }

    suspend fun fetchAnimeDetail(id: Int): AnimeDetailResponse {
        return getBody("anime/$id")
    }

    suspend fun fetchAnimeFullDetail(id: Int): AnimeDetailResponse {
        return getBody("anime/$id/full")
    }

    suspend fun fetchAnimeEpisodes(id: Int): AnimeEpisodesResponse {
        return getBody("anime/$id/episodes")
    }

    suspend fun fetchRelationEntryPreview(
        id: Int,
        type: String?
    ): RelationEntryPreviewResponse {
        val resource = when (type?.lowercase()) {
            "manga" -> "manga"
            else -> "anime"
        }

        return getBody("$resource/$id")
    }

    private fun HttpRequestBuilder.applyCommonSearchParameters(
        filters: MediaSearchFilters,
        page: Int
    ) {
        parameter("page", page.coerceAtLeast(1))
        parameter("limit", filters.limit.toIntOrNull()?.coerceIn(1, 25) ?: 12)
        parameterIfNotBlank("q", filters.query)
        parameterIfNotBlank("score", filters.score)
        parameterIfNotBlank("min_score", filters.minScore)
        parameterIfNotBlank("max_score", filters.maxScore)
        parameter("sfw", filters.sfw)
        parameterIfNotBlank("genres", filters.genres)
        parameterIfNotBlank("genres_exclude", filters.genresExclude)
        parameterIfNotBlank("order_by", filters.orderBy)
        parameterIfNotBlank("sort", filters.sort)
        parameterIfNotBlank("letter", filters.letter)
        parameterIfNotBlank("start_date", filters.startDate)
        parameterIfNotBlank("end_date", filters.endDate)

        if (filters.unapproved) {
            parameter("unapproved", "")
        }
    }

    private fun HttpRequestBuilder.parameterIfNotBlank(
        name: String,
        value: String?
    ) {
        value
            ?.trim()
            ?.takeIf { it.isNotBlank() }
            ?.let { parameter(name, it) }
    }

    private suspend inline fun <reified T> getBody(
        path: String,
        crossinline builder: HttpRequestBuilder.() -> Unit = {}
    ): T {
        JikanRateLimiter.awaitTurn()

        val response = client.get("$BASE_URL$path") {
            builder()
        }

        response.throwIfError()
        return response.body()
    }

    private suspend fun HttpResponse.throwIfError() {
        if (status == HttpStatusCode.NotModified) {
            throw JikanNotModifiedException()
        }

        if (status.value in 200..299) return

        val responseText = runCatching { bodyAsText() }.getOrDefault("")
        throw parseErrorResponse(
            statusCode = status.value,
            responseText = responseText
        )
    }

    private fun parseErrorResponse(
        statusCode: Int,
        responseText: String
    ): JikanApiException {
        val errorObject = runCatching {
            json.parseToJsonElement(responseText).jsonObject
        }.getOrNull()

        val type = errorObject
            ?.get("type")
            ?.jsonPrimitive
            ?.contentOrNull
            ?: "Http$statusCode"
        val message = errorObject
            ?.get("message")
            ?.jsonPrimitive
            ?.contentOrNull
            ?: responseText.take(300).ifBlank { "Jikan request failed with HTTP $statusCode" }
        val error = errorObject
            ?.get("error")
            ?.jsonPrimitive
            ?.contentOrNull

        return JikanApiException(
            statusCode = statusCode,
            type = type,
            message = message,
            error = error
        )
    }
}