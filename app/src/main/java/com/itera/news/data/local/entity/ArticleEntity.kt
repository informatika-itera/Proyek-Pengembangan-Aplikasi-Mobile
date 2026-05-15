package com.itera.news.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.itera.news.domain.model.Article

@Entity(tableName = "bookmarked_articles")
data class ArticleEntity(
    @PrimaryKey
    val url: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val publishedAt: String,
    val sourceName: String
) {
    fun toDomain(): Article {
        return Article(title, description, url, imageUrl, publishedAt, sourceName)
    }
}

fun Article.toEntity(): ArticleEntity {
    return ArticleEntity(url, title, description, imageUrl, publishedAt, sourceName)
}