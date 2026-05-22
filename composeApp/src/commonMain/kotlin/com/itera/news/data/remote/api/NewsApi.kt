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
    }

    suspend fun getMbgNews(query: String? = null): NewsResponseDto {
        val searchQuery = query ?: "makan bergizi gratis OR gizi anak"
        return httpClient.get(BASE_URL) {
            parameter("q", searchQuery)
            parameter("language", "id")
            parameter("sortBy", "publishedAt")
            parameter("apiKey", apiKey)
        }.body()
    }
}