package com.itera.news.domain.repository

import com.itera.news.domain.model.Article
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    suspend fun getMbgNews(query: String? = null): Result<List<Article>>
    suspend fun saveArticle(article: Article)
    suspend fun deleteArticle(article: Article)
    fun getBookmarkedArticles(): Flow<List<Article>>
    fun isArticleBookmarked(url: String): Flow<Boolean>
    suspend fun updateArticle(article: Article)
    fun getArticleByUrl(url: String): Flow<Article?>
}