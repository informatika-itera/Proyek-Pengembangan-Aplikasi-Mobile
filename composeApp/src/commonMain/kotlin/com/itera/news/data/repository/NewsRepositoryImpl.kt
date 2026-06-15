package com.itera.news.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.itera.news.core.util.currentTimeMillis
import com.itera.news.data.local.NewsDatabase
import com.itera.news.data.remote.api.NewsApi
import com.itera.news.domain.model.Article
import com.itera.news.domain.repository.NewsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementasi repository dengan in-memory cache untuk hemat API.
 *
 * Strategi cache:
 * - TTL = 30 menit: dalam rentang ini, tidak ada request ke NewsAPI
 * - Cache per query-key: default query dan custom search di-cache terpisah
 * - Cache di-invalidate otomatis setelah TTL habis
 */
class NewsRepositoryImpl(
    private val newsApi: NewsApi,
    private val db: NewsDatabase
) : NewsRepository {

    private val queries = db.articleQueries

    // --- In-Memory Cache ---
    private data class CacheEntry(
        val articles: List<Article>,
        val fetchedAt: Long = currentTimeMillis()
    )

    private val cache = mutableMapOf<String, CacheEntry>()

    companion object {
        /** Cache berlaku selama 30 menit (dalam milidetik) */
        private const val CACHE_TTL_MS = 30 * 60 * 1000L
        /** Key default ketika tidak ada custom query */
        private const val DEFAULT_KEY = "__mbg_default__"
    }

    private fun isCacheValid(entry: CacheEntry): Boolean {
        return (currentTimeMillis() - entry.fetchedAt) < CACHE_TTL_MS
    }

    override suspend fun getMbgNews(query: String?): Result<List<Article>> {
        val cacheKey = if (query.isNullOrBlank()) DEFAULT_KEY else query.trim().lowercase()

        // Cek cache — jika masih valid, langsung kembalikan tanpa API call
        val cached = cache[cacheKey]
        if (cached != null && isCacheValid(cached)) {
            return Result.success(cached.articles)
        }

        // Cache miss / expired → fetch dari API
        return try {
            val response = newsApi.getMbgNews(query)
            val articles = response.articles
                ?.filter { it.title != null && it.title != "[Removed]" } // filter artikel yang dihapus
                ?.map { it.toDomain() }
                ?: emptyList()

            // Simpan ke cache
            cache[cacheKey] = CacheEntry(articles)

            Result.success(articles)
        } catch (e: Exception) {
            // Jika gagal tapi ada cache lama → pakai cache lama daripada error
            val staleCache = cache[cacheKey]
            if (staleCache != null) {
                Result.success(staleCache.articles)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun saveArticle(article: Article) {
        queries.insertArticle(
            url = article.url,
            title = article.title,
            description = article.description,
            imageUrl = article.imageUrl,
            publishedAt = article.publishedAt,
            sourceName = article.sourceName,
            category = article.category
        )
    }

    override suspend fun deleteArticle(article: Article) {
        queries.deleteArticle(article.url)
    }

    override fun getBookmarkedArticles(): Flow<List<Article>> {
        return queries.getAllBookmarkedArticles()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { entity ->
                    Article(
                        title = entity.title,
                        description = entity.description,
                        url = entity.url,
                        imageUrl = entity.imageUrl,
                        publishedAt = entity.publishedAt,
                        sourceName = entity.sourceName,
                        category = entity.category
                    )
                }
            }
    }

    override fun isArticleBookmarked(url: String): Flow<Boolean> {
        return queries.isArticleBookmarked(url)
            .asFlow()
            .mapToOne(Dispatchers.IO)
            .map { it > 0 }
    }

    override suspend fun updateArticle(article: Article) {
        queries.updateArticle(
            title = article.title,
            description = article.description,
            imageUrl = article.imageUrl,
            publishedAt = article.publishedAt,
            sourceName = article.sourceName,
            category = article.category,
            url = article.url
        )
    }

    override fun getArticleByUrl(url: String): Flow<Article?> {
        return queries.getArticleByUrl(url)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { entity ->
                entity?.let {
                    Article(
                        title = it.title,
                        description = it.description,
                        url = it.url,
                        imageUrl = it.imageUrl,
                        publishedAt = it.publishedAt,
                        sourceName = it.sourceName,
                        category = it.category
                    )
                }
            }
    }
}

