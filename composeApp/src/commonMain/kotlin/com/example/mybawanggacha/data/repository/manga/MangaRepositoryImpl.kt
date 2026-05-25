package com.example.mybawanggacha.data.repository.manga

import com.example.mybawanggacha.core.coroutines.AppDispatchers
import com.example.mybawanggacha.data.local.source.MangaDetailCacheLocalDataSource
import com.example.mybawanggacha.data.local.source.MediaPageCacheLocalDataSource
import com.example.mybawanggacha.data.local.source.RelationPreviewCacheLocalDataSource
import com.example.mybawanggacha.data.remote.jikan.dto.JikanAnimeListResponse
import com.example.mybawanggacha.data.remote.jikan.dto.JikanRecommendationsResponse
import com.example.mybawanggacha.data.remote.jikan.dto.AnimeRelationEntryDto
import com.example.mybawanggacha.data.remote.jikan.dto.MangaDetailData
import com.example.mybawanggacha.data.remote.jikan.mapper.toDomain
import com.example.mybawanggacha.data.remote.jikan.mapper.previewKey
import com.example.mybawanggacha.data.remote.jikan.mapper.toMangaDomain
import com.example.mybawanggacha.data.remote.jikan.mapper.toMangaDomainPage
import com.example.mybawanggacha.data.remote.jikan.mapper.toSummary
import com.example.mybawanggacha.data.remote.jikan.source.JikanMangaRemoteDataSource
import com.example.mybawanggacha.data.repository.jikan.JikanResponseCacheCodec
import com.example.mybawanggacha.domain.manga.model.MangaDetail
import com.example.mybawanggacha.domain.manga.model.MangaPage
import com.example.mybawanggacha.domain.manga.model.MangaRelationPreview
import com.example.mybawanggacha.domain.manga.model.MangaSummary
import com.example.mybawanggacha.domain.manga.repository.MangaRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

private const val JIKAN_MANGA_RELATION_REQUEST_SPACING_MS = 360L

class MangaRepositoryImpl(
    private val remoteDataSource: JikanMangaRemoteDataSource,
    private val detailCacheLocalDataSource: MangaDetailCacheLocalDataSource,
    private val pageCacheLocalDataSource: MediaPageCacheLocalDataSource,
    private val relationPreviewCacheLocalDataSource: RelationPreviewCacheLocalDataSource,
    private val dispatchers: AppDispatchers
) : MangaRepository {

    private val memoryRelationPreviewCache = mutableMapOf<String, MangaRelationPreview>()

    override suspend fun getTopMangaPage(page: Int): MangaPage = withContext(dispatchers.default) {
        remoteDataSource.fetchTopManga(page = page, type = "manga")
            .toMangaDomainPage(requestedPage = page)
    }

    override suspend fun getPopularMangaPage(page: Int): MangaPage = withContext(dispatchers.default) {
        remoteDataSource.fetchTopManga(page = page, filter = "bypopularity")
            .toMangaDomainPage(requestedPage = page)
    }

    override suspend fun getRecommendations(): List<MangaSummary> = withContext(dispatchers.default) {
        getCachedRecommendations(
            cacheKey = "manga:recommendations",
            fetchRemote = { remoteDataSource.fetchMangaRecommendations() }
        ).data
            .flatMap { it.entry }
            .distinctBy { it.mal_id }
            .map { entry ->
                MangaSummary(
                    malId = entry.mal_id,
                    title = entry.title,
                    imageUrl = entry.images.jpg.large_image_url
                        ?: entry.images.jpg.image_url
                )
            }
    }

    override suspend fun getRandomManga(): MangaSummary = withContext(dispatchers.default) {
        getRandomMangaPicks(count = 1).firstOrNull()
            ?: remoteDataSource.fetchRandomManga().data.toSummary()
    }

    override suspend fun getRandomMangaPicks(count: Int): List<MangaSummary> = withContext(dispatchers.default) {
        getCachedRandomMangaPicks(
            cacheKey = "manga:random:picks:$count",
            count = count
        ).map { detail -> detail.toSummary() }
    }

    private suspend fun getCachedMangaList(
        cacheKey: String,
        fetchRemote: suspend () -> JikanAnimeListResponse
    ): JikanAnimeListResponse {
        val cached = runCatching { pageCacheLocalDataSource.getPage(cacheKey) }.getOrNull()

        if (cached?.isFresh() == true) {
            return JikanResponseCacheCodec.decodeAnimeList(cached.payloadJson)
        }

        return runCatching {
            fetchRemote().also { response ->
                runCatching {
                    pageCacheLocalDataSource.savePage(
                        cacheKey = cacheKey,
                        payloadJson = JikanResponseCacheCodec.encodeAnimeList(response)
                    )
                }
            }
        }.getOrElse { error ->
            if (cached != null) {
                JikanResponseCacheCodec.decodeAnimeList(cached.payloadJson)
            } else {
                throw error
            }
        }
    }

    private suspend fun getCachedRecommendations(
        cacheKey: String,
        fetchRemote: suspend () -> JikanRecommendationsResponse
    ): JikanRecommendationsResponse {
        val cached = runCatching { pageCacheLocalDataSource.getPage(cacheKey) }.getOrNull()

        if (cached?.isFresh() == true) {
            return JikanResponseCacheCodec.decodeRecommendations(cached.payloadJson)
        }

        return runCatching {
            fetchRemote().also { response ->
                runCatching {
                    pageCacheLocalDataSource.savePage(
                        cacheKey = cacheKey,
                        payloadJson = JikanResponseCacheCodec.encodeRecommendations(response)
                    )
                }
            }
        }.getOrElse { error ->
            if (cached != null) {
                JikanResponseCacheCodec.decodeRecommendations(cached.payloadJson)
            } else {
                throw error
            }
        }
    }

    private suspend fun getCachedRandomMangaPicks(
        cacheKey: String,
        count: Int
    ): List<MangaDetailData> {
        val cached = runCatching { pageCacheLocalDataSource.getPage(cacheKey) }.getOrNull()

        if (cached?.isFresh() == true) {
            return JikanResponseCacheCodec.decodeMangaDetails(cached.payloadJson)
        }

        return runCatching {
            buildList {
                repeat(count.coerceAtLeast(1)) { index ->
                    runCatching { remoteDataSource.fetchRandomManga().data }
                        .getOrNull()
                        ?.let { detail -> add(detail) }

                    if (index != count - 1) {
                        delay(JIKAN_MANGA_RELATION_REQUEST_SPACING_MS)
                    }
                }
            }
                .distinctBy { detail -> detail.mal_id }
                .also { details ->
                    if (details.isNotEmpty()) {
                        runCatching {
                            pageCacheLocalDataSource.savePage(
                                cacheKey = cacheKey,
                                payloadJson = JikanResponseCacheCodec.encodeMangaDetails(details)
                            )
                        }
                    }
                }
                .takeIf { details -> details.isNotEmpty() }
                ?: error("Gagal memuat random manga")
        }.getOrElse { error ->
            if (cached != null) {
                JikanResponseCacheCodec.decodeMangaDetails(cached.payloadJson)
            } else {
                throw error
            }
        }
    }

    override suspend fun getMangaDetail(
        malId: Int,
        forceRefresh: Boolean
    ): MangaDetail = withContext(dispatchers.default) {
        val cachedDetail = runCatching {
            detailCacheLocalDataSource.getMangaDetail(malId)
        }.getOrNull()

        if (!forceRefresh && cachedDetail?.isFresh() == true) {
            return@withContext buildMangaDetail(
                mangaDto = MangaDetailCacheCodec.decodeDetail(cachedDetail.detailJson),
                loadRelationPreviews = true
            )
        }

        runCatching {
            fetchRemoteMangaDetail(malId)
        }.getOrElse { error ->
            if (cachedDetail != null) {
                buildMangaDetail(
                    mangaDto = MangaDetailCacheCodec.decodeDetail(cachedDetail.detailJson),
                    loadRelationPreviews = true
                )
            } else {
                throw error
            }
        }
    }

    private suspend fun fetchRemoteMangaDetail(malId: Int): MangaDetail {
        val mangaDto = remoteDataSource.fetchMangaFullDetail(malId).data

        runCatching {
            detailCacheLocalDataSource.saveMangaDetail(
                malId = mangaDto.mal_id,
                detailJson = MangaDetailCacheCodec.encodeDetail(mangaDto)
            )
        }

        return buildMangaDetail(
            mangaDto = mangaDto,
            loadRelationPreviews = true
        )
    }

    private suspend fun buildMangaDetail(
        mangaDto: MangaDetailData,
        loadRelationPreviews: Boolean
    ): MangaDetail {
        val relationPreviews = if (loadRelationPreviews) {
            fetchRelationPreviews(entries = mangaDto.relations.flatMap { it.entry })
        } else {
            emptyMap()
        }

        return mangaDto.toDomain(relationPreviews = relationPreviews)
    }

    private suspend fun fetchRelationPreviews(
        entries: List<AnimeRelationEntryDto>
    ): Map<String, MangaRelationPreview> {
        val previews = mutableMapOf<String, MangaRelationPreview>()

        entries.distinctBy { it.previewKey() }.forEachIndexed { index, entry ->
            val key = entry.previewKey()
            memoryRelationPreviewCache[key]?.let { preview ->
                previews[key] = preview
                return@forEachIndexed
            }

            val cachedPreview = runCatching { relationPreviewCacheLocalDataSource.getPreview(key) }.getOrNull()
            if (cachedPreview?.isFresh() == true) {
                val preview = JikanResponseCacheCodec.decodeRelationPreview(cachedPreview.previewJson)
                    .toMangaDomain()
                memoryRelationPreviewCache[key] = preview
                previews[key] = preview
                return@forEachIndexed
            }

            if (index > 0) delay(JIKAN_MANGA_RELATION_REQUEST_SPACING_MS)

            val remotePreview = runCatching {
                remoteDataSource.fetchRelationEntryPreview(id = entry.mal_id, type = entry.type).data
            }.getOrNull()

            if (remotePreview != null) {
                val preview = remotePreview.toMangaDomain()
                runCatching {
                    relationPreviewCacheLocalDataSource.savePreview(
                        cacheKey = key,
                        previewJson = JikanResponseCacheCodec.encodeRelationPreview(remotePreview)
                    )
                }
                memoryRelationPreviewCache[key] = preview
                previews[key] = preview
            } else if (cachedPreview != null) {
                val preview = JikanResponseCacheCodec.decodeRelationPreview(cachedPreview.previewJson)
                    .toMangaDomain()
                memoryRelationPreviewCache[key] = preview
                previews[key] = preview
            }
        }

        return previews
    }
}