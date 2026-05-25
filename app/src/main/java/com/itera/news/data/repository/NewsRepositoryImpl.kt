package com.itera.news.data.repository

import android.util.Log
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class NewsRepositoryImpl(
    private val api: NewsApi,
    private val dao: ArticleDao,
    private val geminiService: GeminiService
) : NewsRepository {

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
                            val category = try {
                                geminiService.categorizeNews(domainArticle.title, domainArticle.description)
                            } catch (e: Exception) {
                                Log.e("NewsRepository", "Gemini categorization failed", e)
                                "Netral"
                            }
                            domainArticle.copy(category = category)
                        }
                    }.awaitAll()
                }
                
                // Cache articles for offline use
                try {
                    dao.insertCachedArticles(articles.map { it.toEntity() })
                } catch (e: Exception) {
                    Log.e("NewsRepository", "Failed to cache articles", e)
                }
                
                emit(Result.success(articles))
            } else {
                // Try to load from cache if API fails
                val cachedArticles = dao.getCachedArticles().first().map { it.toDomain() }
                if (cachedArticles.isNotEmpty()) {
                    emit(Result.success(cachedArticles))
                } else {
                    emit(Result.failure(Exception("Gagal mengambil data berita")))
                }
            }
        } catch (e: Exception) {
            Log.e("NewsRepository", "Network error, loading from cache", e)
            // Load from cache when offline
            try {
                val cachedArticles = dao.getCachedArticles().first().map { it.toDomain() }
                if (cachedArticles.isNotEmpty()) {
                    emit(Result.success(cachedArticles))
                } else {
                    emit(Result.failure(Exception("Tidak ada koneksi internet dan cache kosong")))
                }
            } catch (cacheError: Exception) {
                emit(Result.failure(Exception("Gagal memuat data: ${e.message}")))
            }
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
    
    override fun getCachedArticles(): Flow<List<Article>> {
        return dao.getCachedArticles().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun clearCache() {
        dao.clearOldCache()
    }
}
