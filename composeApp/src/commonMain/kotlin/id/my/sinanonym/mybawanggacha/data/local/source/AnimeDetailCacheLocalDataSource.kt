package id.my.sinanonym.mybawanggacha.data.local.source

import id.my.sinanonym.mybawanggacha.core.coroutines.AppDispatchers
import id.my.sinanonym.mybawanggacha.data.local.NoteDatabase
import id.my.sinanonym.mybawanggacha.data.remote.jikan.dto.AnimeDetailData
import id.my.sinanonym.mybawanggacha.data.remote.jikan.dto.AnimeEpisodeDto
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlin.time.Clock

const val ANIME_DETAIL_CACHE_MAX_AGE_MS: Long = 12L * 60L * 60L * 1_000L

data class CachedAnimeDetail(
    val detail: AnimeDetailData,
    val episodes: List<AnimeEpisodeDto>,
    val cachedAt: Long
) {
    fun isFresh(
        nowMillis: Long = Clock.System.now().toEpochMilliseconds(),
        maxAgeMillis: Long = ANIME_DETAIL_CACHE_MAX_AGE_MS
    ): Boolean {
        return nowMillis - cachedAt <= maxAgeMillis
    }
}

internal object AnimeDetailCacheCodec {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun encodeDetail(detail: AnimeDetailData): String {
        return json.encodeToString(AnimeDetailData.serializer(), detail)
    }

    fun decodeDetail(value: String): AnimeDetailData {
        return json.decodeFromString(AnimeDetailData.serializer(), value)
    }

    fun encodeEpisodes(episodes: List<AnimeEpisodeDto>): String {
        return json.encodeToString(ListSerializer(AnimeEpisodeDto.serializer()), episodes)
    }

    fun decodeEpisodes(value: String): List<AnimeEpisodeDto> {
        return json.decodeFromString(ListSerializer(AnimeEpisodeDto.serializer()), value)
    }
}

class AnimeDetailCacheLocalDataSource(
    database: NoteDatabase,
    private val dispatchers: AppDispatchers
) {
    private val queries = database.animeQueries

    suspend fun getAnimeDetail(malId: Int): CachedAnimeDetail? = withContext(dispatchers.io) {
        queries.getAnimeDetailCache(malId.toLong())
            .executeAsOneOrNull()
            ?.let { entity ->
                CachedAnimeDetail(
                    detail = AnimeDetailCacheCodec.decodeDetail(entity.detail_json),
                    episodes = AnimeDetailCacheCodec.decodeEpisodes(entity.episodes_json),
                    cachedAt = entity.cached_at
                )
            }
    }

    suspend fun saveAnimeDetail(
        detail: AnimeDetailData,
        episodes: List<AnimeEpisodeDto>
    ) = withContext(dispatchers.io) {
        queries.upsertAnimeDetailCache(
            anime_id = detail.mal_id.toLong(),
            detail_json = AnimeDetailCacheCodec.encodeDetail(detail),
            episodes_json = AnimeDetailCacheCodec.encodeEpisodes(episodes),
            cached_at = Clock.System.now().toEpochMilliseconds()
        )
    }
}
