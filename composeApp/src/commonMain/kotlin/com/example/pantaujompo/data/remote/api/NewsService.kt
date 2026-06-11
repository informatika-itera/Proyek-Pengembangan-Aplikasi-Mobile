package com.example.pantaujompo.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.Serializable

@Serializable
data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<NewsArticleDto>
)

@Serializable
data class NewsArticleDto(
    val source: SourceDto,
    val title: String?,
    val description: String?,
    val content: String?,
    val urlToImage: String?,
    val url: String?,
    val publishedAt: String?
)

@Serializable
data class SourceDto(
    val name: String?
)

class NewsService(private val httpClient: HttpClient) {
    suspend fun fetchNews(query: String = "kesehatan OR olahraga"): NewsResponse {
        return httpClient.get("https://newsapi.org/v2/everything") {
            parameter("q", query)
            parameter("language", "id")
            parameter("sortBy", "publishedAt")
            parameter("apiKey", "b73cac97bf0b4e1dbaef1a365a46acea") // Replace with secure key management in prod
        }.body()
    }
}
