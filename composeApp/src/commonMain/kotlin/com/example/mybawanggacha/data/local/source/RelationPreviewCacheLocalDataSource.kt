package com.example.mybawanggacha.data.local.source

import com.example.mybawanggacha.core.coroutines.AppDispatchers
import com.example.mybawanggacha.data.local.NoteDatabase
import kotlinx.coroutines.withContext
import kotlin.time.Clock

const val RELATION_PREVIEW_CACHE_MAX_AGE_MS: Long = 12L * 60L * 60L * 1_000L

data class CachedRelationPreviewPayload(
    val previewJson: String,
    val cachedAt: Long
) {
    fun isFresh(
        nowMillis: Long = Clock.System.now().toEpochMilliseconds(),
        maxAgeMillis: Long = RELATION_PREVIEW_CACHE_MAX_AGE_MS
    ): Boolean {
        return nowMillis - cachedAt <= maxAgeMillis
    }
}

class RelationPreviewCacheLocalDataSource(
    database: NoteDatabase,
    private val dispatchers: AppDispatchers
) {
    private val queries = database.mediaCacheQueries

    suspend fun getPreview(cacheKey: String): CachedRelationPreviewPayload? = withContext(dispatchers.io) {
        queries.getRelationPreviewCache(cacheKey)
            .executeAsOneOrNull()
            ?.let { entity ->
                CachedRelationPreviewPayload(
                    previewJson = entity.preview_json,
                    cachedAt = entity.cached_at
                )
            }
    }

    suspend fun savePreview(
        cacheKey: String,
        previewJson: String
    ) = withContext(dispatchers.io) {
        queries.upsertRelationPreviewCache(
            cache_key = cacheKey,
            preview_json = previewJson,
            cached_at = Clock.System.now().toEpochMilliseconds()
        )
    }
}
