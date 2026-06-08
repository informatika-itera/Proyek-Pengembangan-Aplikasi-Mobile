package id.my.sinanonym.mybawanggacha.data.repository.search

import id.my.sinanonym.mybawanggacha.core.coroutines.AppDispatchers
import id.my.sinanonym.mybawanggacha.data.local.source.CachedMediaPagePayload
import id.my.sinanonym.mybawanggacha.data.local.source.MediaPageCacheLocalDataSource
import id.my.sinanonym.mybawanggacha.data.remote.jikan.dto.JikanAnimeListResponse
import id.my.sinanonym.mybawanggacha.data.remote.jikan.mapper.toSearchPage
import id.my.sinanonym.mybawanggacha.data.remote.jikan.source.JikanSearchRemoteDataSource
import id.my.sinanonym.mybawanggacha.data.repository.jikan.AlwaysOnlineJikanCachePolicy
import id.my.sinanonym.mybawanggacha.data.repository.jikan.JikanCachePolicy
import id.my.sinanonym.mybawanggacha.data.repository.jikan.JikanResponseCacheCodec
import id.my.sinanonym.mybawanggacha.domain.search.model.MediaSearchFilters
import id.my.sinanonym.mybawanggacha.domain.search.model.MediaSearchPage
import id.my.sinanonym.mybawanggacha.domain.search.model.SearchFilterMetadata
import id.my.sinanonym.mybawanggacha.domain.search.model.SearchMediaType
import id.my.sinanonym.mybawanggacha.domain.search.repository.SearchRepository
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val SEARCH_METADATA_ANIME_CACHE_KEY = "search_metadata:anime"
private const val SEARCH_METADATA_MANGA_CACHE_KEY = "search_metadata:manga"

class SearchRepositoryImpl(
    private val remoteDataSource: JikanSearchRemoteDataSource,
    private val dispatchers: AppDispatchers,
    private val mediaPageCacheLocalDataSource: MediaPageCacheLocalDataSource? = null,
    private val cachePolicy: JikanCachePolicy = AlwaysOnlineJikanCachePolicy
) : SearchRepository {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    override suspend fun search(
        filters: MediaSearchFilters,
        page: Int
    ): MediaSearchPage = withContext(dispatchers.default) {
        when (filters.mediaType) {
            SearchMediaType.Anime -> searchCachedPage(
                filters = filters,
                page = page,
                mediaType = SearchMediaType.Anime,
                fetchRemote = { remoteDataSource.searchAnime(filters = filters, page = page) }
            )

            SearchMediaType.Manga -> searchCachedPage(
                filters = filters,
                page = page,
                mediaType = SearchMediaType.Manga,
                fetchRemote = { remoteDataSource.searchManga(filters = filters, page = page) }
            )
        }
    }

    override suspend fun getFilterMetadata(
        mediaType: SearchMediaType
    ): SearchFilterMetadata = withContext(dispatchers.default) {
        val cacheKey = mediaType.metadataCacheKey()
        val cachedPayload = runCatching {
            mediaPageCacheLocalDataSource?.getPage(cacheKey)
        }.getOrNull()

        cachedPayload
            ?.takeIf { payload -> payload.isFresh() }
            ?.decodeMetadataOrNull()
            ?.let { metadata -> return@withContext metadata }

        if (!cachePolicy.allowsNetwork()) {
            return@withContext cachedPayload?.decodeMetadataOrNull()
                ?: error(cachePolicy.cacheMissMessage("metadata filter"))
        }

        runCatching {
            fetchRemoteFilterMetadata(mediaType).also { metadata ->
                runCatching {
                    mediaPageCacheLocalDataSource?.savePage(
                        cacheKey = cacheKey,
                        payloadJson = json.encodeToString(metadata)
                    )
                }
            }
        }.getOrElse { error ->
            cachedPayload?.decodeMetadataOrNull() ?: throw error
        }
    }

    private suspend fun searchCachedPage(
        filters: MediaSearchFilters,
        page: Int,
        mediaType: SearchMediaType,
        fetchRemote: suspend () -> JikanAnimeListResponse
    ): MediaSearchPage {
        val cacheKey = filters.searchCacheKey(page)
        val cachedPayload = runCatching {
            mediaPageCacheLocalDataSource?.getPage(cacheKey)
        }.getOrNull()

        cachedPayload
            ?.takeIf { payload -> payload.isFresh() }
            ?.decodeSearchPageOrNull(mediaType = mediaType, requestedPage = page)
            ?.let { cachedPage -> return cachedPage }

        if (!cachePolicy.allowsNetwork()) {
            return cachedPayload?.decodeSearchPageOrNull(mediaType = mediaType, requestedPage = page)
                ?: error(cachePolicy.cacheMissMessage("hasil search"))
        }

        return runCatching {
            fetchRemote().also { response ->
                runCatching {
                    mediaPageCacheLocalDataSource?.savePage(
                        cacheKey = cacheKey,
                        payloadJson = JikanResponseCacheCodec.encodeAnimeList(response)
                    )
                }
            }.toSearchPage(mediaType = mediaType, requestedPage = page)
        }.getOrElse { error ->
            cachedPayload?.decodeSearchPageOrNull(mediaType = mediaType, requestedPage = page)
                ?: throw error
        }
    }

    private suspend fun fetchRemoteFilterMetadata(
        mediaType: SearchMediaType
    ): SearchFilterMetadata {
        return when (mediaType) {
            SearchMediaType.Anime -> remoteDataSource.getAnimeFilterMetadata()
            SearchMediaType.Manga -> remoteDataSource.getMangaFilterMetadata()
        }
    }

    private fun SearchMediaType.metadataCacheKey(): String {
        return when (this) {
            SearchMediaType.Anime -> SEARCH_METADATA_ANIME_CACHE_KEY
            SearchMediaType.Manga -> SEARCH_METADATA_MANGA_CACHE_KEY
        }
    }

    private fun CachedMediaPagePayload.decodeMetadataOrNull(): SearchFilterMetadata? {
        return runCatching {
            json.decodeFromString<SearchFilterMetadata>(payloadJson)
        }.getOrNull()
    }

    private fun CachedMediaPagePayload.decodeSearchPageOrNull(
        mediaType: SearchMediaType,
        requestedPage: Int
    ): MediaSearchPage? {
        return runCatching {
            JikanResponseCacheCodec
                .decodeAnimeList(payloadJson)
                .toSearchPage(
                    mediaType = mediaType,
                    requestedPage = requestedPage
                )
        }.getOrNull()
    }

    private fun MediaSearchFilters.searchCacheKey(page: Int): String {
        return buildString {
            append("search:")
            append(mediaType.name.lowercase())
            append(":page=").append(page.coerceAtLeast(1))
            append("|limit=").append(limit.trim())
            append("|q=").append(query.trim())
            append("|type=").append(type.orEmpty().trim())
            append("|status=").append(status.orEmpty().trim())
            append("|rating=").append(rating.orEmpty().trim())
            append("|score=").append(score.trim())
            append("|min=").append(minScore.trim())
            append("|max=").append(maxScore.trim())
            append("|sfw=").append(sfw)
            append("|genres=").append(genres.trim())
            append("|exclude=").append(genresExclude.trim())
            append("|order=").append(orderBy.orEmpty().trim())
            append("|sort=").append(sort.orEmpty().trim())
            append("|letter=").append(letter.trim())
            append("|start=").append(startDate.trim())
            append("|end=").append(endDate.trim())
            append("|unapproved=").append(unapproved)
            append("|producers=").append(producers.trim())
            append("|magazines=").append(magazines.trim())
        }
    }
}