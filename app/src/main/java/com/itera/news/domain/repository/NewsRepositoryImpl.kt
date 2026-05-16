package com.itera.news.data.repository

import com.itera.news.data.local.dao.ArticleDao
import com.itera.news.data.local.entity.toEntity
import com.itera.news.data.remote.GeminiService
import com.itera.news.data.remote.NewsApi
import com.itera.news.domain.model.Article
import com.itera.news.domain.repository.NewsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

// Tambahkan parameter dao: ArticleDao
class NewsRepositoryImpl(
    private val api: NewsApi,
    private val dao: ArticleDao,
    private val geminiService: GeminiService
) : NewsRepository {

    // TODO: Masukkan API Key dari NewsAPI.org di sini (jika belum)
    private val apiKey = "0faefcb90a144faf99a182e7ca3332d9" 

    override fun getMbgNews(query: String?): Flow<Result<List<Article>>> = flow {
        try {
            val searchQuery = query ?: "makan bergizi gratis OR gizi anak"
            val response = api.getMbgNews(query = searchQuery, apiKey = apiKey)
            if (response.status == "ok") {
                val articles = coroutineScope {
                    response.articles.map { dto ->
                        async {
                            val domainArticle = dto.toDomain()
                            val category = geminiService.categorizeNews(domainArticle.title, domainArticle.description)
                            domainArticle.copy(category = category)
                        }
                    }.awaitAll()
                }
                emit(Result.success(articles))
            } else {
                emit(Result.failure(Exception("Gagal mengambil data berita")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getBookmarkedArticles(): Flow<List<Article>> {
        return dao.getBookmarkedArticles().map { entities -> 
            entities.map { it.toDomain() } 
        }
    }

    override fun isArticleBookmarked(url: String): Flow<Boolean> {
        return dao.isArticleBookmarked(url)
    }

    override suspend fun saveArticle(article: Article) {
        dao.insertArticle(article.toEntity())
    }

    override suspend fun deleteArticle(article: Article) {
        dao.deleteArticle(article.toEntity())
    }
}