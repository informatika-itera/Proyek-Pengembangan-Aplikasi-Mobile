package com.itera.news.domain.repository

import com.itera.news.domain.model.Article
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getMbgNews(): Flow<Result<List<Article>>>
    
    // Fitur Bookmark
    fun getBookmarkedArticles(): Flow<List<Article>>
    fun isArticleBookmarked(url: String): Flow<Boolean>
    suspend fun saveArticle(article: Article)
    suspend fun deleteArticle(article: Article)
}