package com.itera.news.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.itera.news.data.local.entity.ArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
    // Bookmark operations — hanya artikel yang isBookmarked = true
    @Query("SELECT * FROM bookmarked_articles WHERE isBookmarked = 1 ORDER BY publishedAt DESC")
    fun getBookmarkedArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarked_articles WHERE url = :url AND isBookmarked = 1)")
    fun isArticleBookmarked(url: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: ArticleEntity)

    @Delete
    suspend fun deleteArticle(article: ArticleEntity)

    // Cache operations — semua artikel (termasuk yg bukan bookmark)
    @Query("SELECT * FROM bookmarked_articles ORDER BY publishedAt DESC LIMIT 50")
    fun getCachedArticles(): Flow<List<ArticleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedArticles(articles: List<ArticleEntity>)

    // Hanya hapus artikel cache (bukan bookmark) yang sudah lama
    @Query("DELETE FROM bookmarked_articles WHERE isBookmarked = 0 AND url NOT IN (SELECT url FROM bookmarked_articles WHERE isBookmarked = 0 ORDER BY publishedAt DESC LIMIT 20)")
    suspend fun clearOldCache()

    @Query("SELECT * FROM bookmarked_articles WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY publishedAt DESC")
    fun searchCachedArticles(query: String): Flow<List<ArticleEntity>>
}
