package com.example.mybawanggacha.data.local.source

import com.example.mybawanggacha.core.coroutines.AppDispatchers
import com.example.mybawanggacha.data.local.NoteDatabase
import kotlinx.coroutines.withContext
import kotlin.time.Clock

const val MANGA_DETAIL_CACHE_MAX_AGE_MS: Long = 12L * 60L * 60L * 1_000L

data class CachedMangaDetailPayload(
    val detailJson: String,
    val cachedAt: Long
) {
    fun isFresh(
        nowMillis: Long = Clock.System.now().toEpochMilliseconds(),
        maxAgeMillis: Long = MANGA_DETAIL_CACHE_MAX_AGE_MS
    ): Boolean {
        return nowMillis - cachedAt <= maxAgeMillis
    }
}

class MangaDetailCacheLocalDataSource(
    database: NoteDatabase,
    private val dispatchers: AppDispatchers
) {
    private val queries = database.mangaQueries

    suspend fun getMangaDetail(malId: Int): CachedMangaDetailPayload? = withContext(dispatchers.io) {
        queries.getMangaDetailCache(malId.toLong())
            .executeAsOneOrNull()
            ?.let { entity ->
                CachedMangaDetailPayload(
                    detailJson = entity.detail_json,
                    cachedAt = entity.cached_at
                )
            }
    }

    suspend fun saveMangaDetail(
        malId: Int,
        detailJson: String
    ) = withContext(dispatchers.io) {
        queries.upsertMangaDetailCache(
            manga_id = malId.toLong(),
            detail_json = detailJson,
            cached_at = Clock.System.now().toEpochMilliseconds()
        )
    }
}
