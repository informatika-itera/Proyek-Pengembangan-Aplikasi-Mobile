package com.itera.news

import com.itera.news.domain.model.Article
import com.itera.news.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeNewsRepository : NewsRepository {
    val bookmarkedArticlesFlow = MutableStateFlow<List<Article>>(emptyList())
    var shouldReturnError = false

    override suspend fun getMbgNews(query: String?): Result<List<Article>> {
        return if (shouldReturnError) Result.failure(Exception("Network Error"))
        else Result.success(emptyList())
    }

    override suspend fun saveArticle(article: Article) {
        val current = bookmarkedArticlesFlow.value.toMutableList()
        current.add(article)
        bookmarkedArticlesFlow.value = current
    }

    override suspend fun deleteArticle(article: Article) {
        val current = bookmarkedArticlesFlow.value.toMutableList()
        current.remove(article)
        bookmarkedArticlesFlow.value = current
    }

    override fun getBookmarkedArticles(): Flow<List<Article>> = bookmarkedArticlesFlow

    override fun isArticleBookmarked(url: String): Flow<Boolean> =
        bookmarkedArticlesFlow.map { list -> list.any { it.url == url } }

    override suspend fun updateArticle(article: Article) {
        val current = bookmarkedArticlesFlow.value.toMutableList()
        val index = current.indexOfFirst { it.url == article.url }
        if (index != -1) {
            current[index] = article
            bookmarkedArticlesFlow.value = current
        }
    }

    override fun getArticleByUrl(url: String): Flow<Article?> =
        bookmarkedArticlesFlow.map { list -> list.find { it.url == url } }
}