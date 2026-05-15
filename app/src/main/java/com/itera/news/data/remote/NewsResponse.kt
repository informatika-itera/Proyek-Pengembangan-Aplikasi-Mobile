package com.itera.news.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.itera.news.domain.model.Article

data class NewsResponse(
    @SerializedName("status") val status: String,
    @SerializedName("totalResults") val totalResults: Int,
    @SerializedName("articles") val articles: List<ArticleDto>
)

data class ArticleDto(
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("urlToImage") val urlToImage: String?,
    @SerializedName("publishedAt") val publishedAt: String?,
    @SerializedName("source") val source: SourceDto?
) {
    fun toDomain(): Article {
        return Article(
            title = title ?: "",
            description = description ?: "",
            url = url ?: "",
            imageUrl = urlToImage ?: "",
            publishedAt = publishedAt ?: "",
            sourceName = source?.name ?: ""
        )
    }
}

data class SourceDto(
    @SerializedName("name") val name: String?
)