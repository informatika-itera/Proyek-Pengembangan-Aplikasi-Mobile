package com.example.mybawanggacha.data.local.source

import com.example.mybawanggacha.core.coroutines.AppDispatchers
import com.example.mybawanggacha.data.local.NoteDatabase
import kotlinx.coroutines.withContext
import kotlin.time.Clock

const val MEDIA_PAGE_CACHE_MAX_AGE_MS: Long = 24L * 60L * 60L * 1_000L

data class CachedMediaPagePayload(
    val payloadJson: String,
    val cachedAt: Long
) {
    fun isFresh(
        nowMillis: Long = Clock.System.now().toEpochMilliseconds(),
        maxAgeMillis: Long = MEDIA_PAGE_CACHE_MAX_AGE_MS
    ): Boolean {
        return nowMillis - cachedAt <= maxAgeMillis
    }
}

class MediaPageCacheLocalDataSource(
    database: NoteDatabase,
    private val dispatchers: AppDispatchers
) {
    private val queries = database.mediaCacheQueries

    suspend fun getPage(cacheKey: String): CachedMediaPagePayload? = withContext(dispatchers.io) {
        queries.getMediaPageCache(cacheKey)
            .executeAsOneOrNull()
            ?.let { entity ->
                CachedMediaPagePayload(
                    payloadJson = entity.payload_json,
                    cachedAt = entity.cached_at
                )
            }
    }

    suspend fun savePage(
        cacheKey: String,
        payloadJson: String
    ) = withContext(dispatchers.io) {
        queries.upsertMediaPageCache(
            cache_key = cacheKey,
            payload_json = payloadJson,
            cached_at = Clock.System.now().toEpochMilliseconds()
        )
    }
}
