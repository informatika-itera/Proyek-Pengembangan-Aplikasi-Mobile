package com.itera.news.data.remote.api

import com.itera.news.data.remote.dto.NewsResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class NewsApi(
    private val httpClient: HttpClient,
    private val apiKey: String
) {
    companion object {
        private const val BASE_URL = "https://newsapi.org/v2/everything"
        // Query tetap pada topik MBG — tidak berubah agar cache maksimal
        private const val DEFAULT_QUERY = "\"makan bergizi gratis\" OR \"program MBG\""
        // Ambil 20 artikel saja — cukup untuk tampilan, hemat kuota API
        private const val PAGE_SIZE = 20
    }

    /**
     * Ambil berita MBG. Jika [query] null, gunakan query default MBG.
     * Selalu filter bahasa Indonesia agar relevan.
     */
    suspend fun getMbgNews(query: String? = null): NewsResponseDto {
        val searchQuery = if (query.isNullOrBlank()) DEFAULT_QUERY
                         else "\"$query\" AND (\"makan bergizi\" OR MBG)"
        return httpClient.get(BASE_URL) {
            parameter("q", searchQuery)
            parameter("language", "id")
            parameter("sortBy", "publishedAt")
            parameter("pageSize", PAGE_SIZE)
            parameter("apiKey", apiKey)
        }.body()
    }
}