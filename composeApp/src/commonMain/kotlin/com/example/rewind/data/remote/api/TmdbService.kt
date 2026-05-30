package com.example.rewind.data.remote.api

import com.example.rewind.data.remote.dto.TmdbGenreListDto
import com.example.rewind.data.remote.dto.TmdbMovieDetailDto
import com.example.rewind.data.remote.dto.TmdbSearchResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

/**
 * Service untuk komunikasi dengan TMDB API v3
 *
 * Base URL: https://api.themoviedb.org/3
 * Auth: API Key via query param `api_key`
 * Docs: https://developer.themoviedb.org/docs
 *
 * Endpoint yang digunakan:
 * - /search/multi        → search film & series sekaligus
 * - /movie/{id}          → detail film
 * - /tv/{id}             → detail series
 * - /trending/all/week   → trending minggu ini
 * - /genre/movie/list    → daftar genre film
 */
class TmdbService(
    private val client: HttpClient,
    private val apiKey: String
) {
    companion object {
        private const val BASE_URL = "https://api.themoviedb.org/3"
        private const val DEFAULT_LANGUAGE = "id-ID"
        private const val FALLBACK_LANGUAGE = "en-US"
    }

    /**
     * Search film DAN series sekaligus (multi-search)
     * Mengembalikan campuran movie & tv dalam satu response
     */
    suspend fun searchMulti(
        query: String,
        page: Int = 1,
        language: String = DEFAULT_LANGUAGE
    ): TmdbSearchResponseDto {
        return client.get("$BASE_URL/search/multi") {
            parameter("api_key", apiKey)
            parameter("query", query)
            parameter("page", page)
            parameter("language", language)
            parameter("include_adult", false)
        }.body()
    }

    /**
     * Search khusus film
     */
    suspend fun searchMovies(
        query: String,
        page: Int = 1,
        language: String = DEFAULT_LANGUAGE
    ): TmdbSearchResponseDto {
        return client.get("$BASE_URL/search/movie") {
            parameter("api_key", apiKey)
            parameter("query", query)
            parameter("page", page)
            parameter("language", language)
            parameter("include_adult", false)
        }.body()
    }

    /**
     * Search khusus TV/series
     */
    suspend fun searchTv(
        query: String,
        page: Int = 1,
        language: String = DEFAULT_LANGUAGE
    ): TmdbSearchResponseDto {
        return client.get("$BASE_URL/search/tv") {
            parameter("api_key", apiKey)
            parameter("query", query)
            parameter("page", page)
            parameter("language", language)
        }.body()
    }

    /**
     * Ambil detail film berdasarkan TMDB ID
     */
    suspend fun getMovieDetail(
        tmdbId: Int,
        language: String = DEFAULT_LANGUAGE
    ): TmdbMovieDetailDto {
        return client.get("$BASE_URL/movie/$tmdbId") {
            parameter("api_key", apiKey)
            parameter("language", language)
        }.body()
    }

    /**
     * Ambil detail TV/series berdasarkan TMDB ID
     */
    suspend fun getTvDetail(
        tmdbId: Int,
        language: String = DEFAULT_LANGUAGE
    ): TmdbMovieDetailDto {
        return client.get("$BASE_URL/tv/$tmdbId") {
            parameter("api_key", apiKey)
            parameter("language", language)
        }.body()
    }

    /**
     * Ambil daftar trending minggu ini (film + series)
     */
    suspend fun getTrending(
        timeWindow: String = "week",  // "day" | "week"
        language: String = DEFAULT_LANGUAGE
    ): TmdbSearchResponseDto {
        return client.get("$BASE_URL/trending/all/$timeWindow") {
            parameter("api_key", apiKey)
            parameter("language", language)
        }.body()
    }

    /**
     * Ambil daftar genre film dari TMDB
     */
    suspend fun getMovieGenres(
        language: String = DEFAULT_LANGUAGE
    ): TmdbGenreListDto {
        return client.get("$BASE_URL/genre/movie/list") {
            parameter("api_key", apiKey)
            parameter("language", language)
        }.body()
    }
}