package com.itera.news.domain.model

data class Article(
    val title: String,
    val description: String,
    val url: String,
    val imageUrl: String,
    val publishedAt: String,
    val sourceName: String
)