package id.my.sinanonym.mybawanggacha.data.repository.anime

import id.my.sinanonym.mybawanggacha.core.coroutines.AppDispatchers
import id.my.sinanonym.mybawanggacha.data.local.source.AnimeDetailCacheLocalDataSource
import id.my.sinanonym.mybawanggacha.data.local.source.AnimeProgressLocalDataSource
import id.my.sinanonym.mybawanggacha.data.local.source.MediaPageCacheLocalDataSource
import id.my.sinanonym.mybawanggacha.data.local.source.RelationPreviewCacheLocalDataSource
import id.my.sinanonym.mybawanggacha.data.remote.jikan.dto.AnimeDetailData
import id.my.sinanonym.mybawanggacha.data.remote.jikan.dto.AnimeEpisodeDto
import id.my.sinanonym.mybawanggacha.data.remote.jikan.mapper.previewKey
import id.my.sinanonym.mybawanggacha.data.remote.jikan.mapper.toDomain
import id.my.sinanonym.mybawanggacha.data.remote.jikan.mapper.toDomainPage
import id.my.sinanonym.mybawanggacha.data.remote.jikan.mapper.toDomainRecentEpisode
import id.my.sinanonym.mybawanggacha.data.remote.jikan.mapper.toSummary
import id.my.sinanonym.mybawanggacha.data.remote.jikan.source.JikanAnimeRemoteDataSource
import id.my.sinanonym.mybawanggacha.data.remote.jikan.dto.AnimeRelationEntryDto
import id.my.sinanonym.mybawanggacha.data.remote.jikan.dto.JikanAnimeListResponse
import id.my.sinanonym.mybawanggacha.data.remote.jikan.dto.JikanRecommendationsResponse
import id.my.sinanonym.mybawanggacha.data.remote.jikan.dto.JikanSeasonArchiveResponse
import id.my.sinanonym.mybawanggacha.data.remote.jikan.dto.WatchEpisodesResponse
import id.my.sinanonym.mybawanggacha.data.repository.jikan.AlwaysOnlineJikanCachePolicy
import id.my.sinanonym.mybawanggacha.data.repository.jikan.JikanCachePolicy
import id.my.sinanonym.mybawanggacha.data.repository.jikan.JikanResponseCacheCodec
import id.my.sinanonym.mybawanggacha.domain.anime.model.AnimeDetailBundle
import id.my.sinanonym.mybawanggacha.domain.anime.model.AnimeEpisode
import id.my.sinanonym.mybawanggacha.domain.anime.model.AnimePage
import id.my.sinanonym.mybawanggacha.domain.anime.model.AnimeRelationPreview
import id.my.sinanonym.mybawanggacha.domain.anime.model.AnimeSeason
import id.my.sinanonym.mybawanggacha.domain.anime.model.AnimeSeasonPeriod
import id.my.sinanonym.mybawanggacha.domain.anime.model.AnimeSummary
import id.my.sinanonym.mybawanggacha.domain.anime.model.RecentAnimeEpisode
import id.my.sinanonym.mybawanggacha.domain.anime.repository.AnimeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

private const val JIKAN_REQUEST_SPACING_MS = 360L

class AnimeRepositoryImpl(
    private val remoteDataSource: JikanAnimeRemoteDataSource,
    private val progressLocalDataSource: AnimeProgressLocalDataSource,
    private val detailCacheLocalDataSource: AnimeDetailCacheLocalDataSource,
    private val pageCacheLocalDataSource: MediaPageCacheLocalDataSource,
    private val relationPreviewCacheLocalDataSource: RelationPreviewCacheLocalDataSource,
    private val dispatchers: AppDispatchers,
    private val cachePolicy: JikanCachePolicy = AlwaysOnlineJikanCachePolicy
) : AnimeRepository {

    private val memoryRelationPreviewCache = mutableMapOf<String, AnimeRelationPreview>()

    override suspend fun getRecommendations(): List<AnimeSummary> = withContext(dispatchers.default) {
        getCachedRecommendations(
            cacheKey = "anime:recommendations",
            fetchRemote = { remoteDataSource.fetchAnimeRecommendations() }
        ).data
            .flatMap { it.entry }
            .distinctBy { it.mal_id }
            .map { entry ->
                AnimeSummary(
                    malId = entry.mal_id,
                    title = entry.title,
                    imageUrl = entry.images.jpg.large_image_url
                        ?: entry.images.jpg.image_url
                )
            }
    }

    override suspend fun getRandomAnime(): AnimeSummary = withContext(dispatchers.default) {
        getRandomAnimePicks(count = 1).firstOrNull()
            ?: remoteDataSource.fetchRandomAnime().data.toSummary()
    }

    override suspend fun getRandomAnimePicks(
        count: Int,
        forceRefresh: Boolean
    ): List<AnimeSummary> = withContext(dispatchers.default) {
        getCachedRandomAnimePicks(
            cacheKey = "anime:random:picks:$count",
            count = count,
            forceRefresh = forceRefresh
        ).map { detail -> detail.toSummary() }
    }

    override suspend fun getRecentEpisodes(): List<RecentAnimeEpisode> = withContext(dispatchers.default) {
        getCachedWatchEpisodes(
            cacheKey = "anime:watch:episodes:1",
            fetchRemote = { remoteDataSource.fetchRecentWatchEpisodes(page = 1) }
        ).data
            .mapNotNull { item -> item.toDomainRecentEpisode() }
            .distinctBy { episode -> "${episode.animeMalId}:${episode.episodeMalId}:${episode.episodeTitle}" }
    }

    override suspend fun getCurrentSeasonAnimePage(page: Int): AnimePage = withContext(dispatchers.default) {
        getCachedAnimeList(
            cacheKey = "anime:season:now:$page",
            fetchRemote = { remoteDataSource.fetchCurrentSeasonAnime(page = page) }
        ).toDomainPage(requestedPage = page)
    }

    override suspend fun getSeasonAnimePage(
        year: Int,
        season: AnimeSeason,
        page: Int
    ): AnimePage = withContext(dispatchers.default) {
        getCachedAnimeList(
            cacheKey = "anime:season:$year:${season.apiKey}:$page",
            fetchRemote = {
                remoteDataSource.fetchSeasonAnime(
                    year = year,
                    season = season.apiKey,
                    page = page
                )
            }
        ).toDomainPage(requestedPage = page)
    }

    override suspend fun getUpcomingSeasonAnimePage(page: Int): AnimePage = withContext(dispatchers.default) {
        getCachedAnimeList(
            cacheKey = "anime:season:upcoming:$page",
            fetchRemote = { remoteDataSource.fetchUpcomingSeasonAnime(page = page) }
        ).toDomainPage(requestedPage = page)
    }

    override suspend fun getTopAnimePage(page: Int): AnimePage = withContext(dispatchers.default) {
        getCachedAnimeList(
            cacheKey = "anime:top:$page",
            fetchRemote = { remoteDataSource.fetchTopAnime(page = page) }
        ).toDomainPage(requestedPage = page)
    }

    override suspend fun getAvailableSeasonPeriods(): List<AnimeSeasonPeriod> = withContext(dispatchers.default) {
        getCachedSeasonArchive(
            cacheKey = "anime:season:archive",
            fetchRemote = { remoteDataSource.fetchSeasonArchive() }
        ).data
            .flatMap { yearDto ->
                yearDto.seasons.mapNotNull { seasonKey ->
                    AnimeSeason.fromApiKey(seasonKey)?.let { season ->
                        AnimeSeasonPeriod(year = yearDto.year, season = season)
                    }
                }
            }
            .distinctBy { it.sortValue }
            .sortedByDescending { it.sortValue }
    }

    private suspend fun getCachedSeasonArchive(
        cacheKey: String,
        fetchRemote: suspend () -> JikanSeasonArchiveResponse
    ): JikanSeasonArchiveResponse {
        val cached = runCatching { pageCacheLocalDataSource.getPage(cacheKey) }.getOrNull()

        if (cached?.isFresh() == true) {
            return JikanResponseCacheCodec.decodeSeasonArchive(cached.payloadJson)
        }

        if (!cachePolicy.allowsNetwork()) {
            return cached?.let { JikanResponseCacheCodec.decodeSeasonArchive(it.payloadJson) }
                ?: error(cachePolicy.cacheMissMessage("arsip musim"))
        }

        return runCatching {
            fetchRemote().also { response ->
                runCatching {
                    pageCacheLocalDataSource.savePage(
                        cacheKey = cacheKey,
                        payloadJson = JikanResponseCacheCodec.encodeSeasonArchive(response)
                    )
                }
            }
        }.getOrElse { error ->
            if (cached != null) {
                JikanResponseCacheCodec.decodeSeasonArchive(cached.payloadJson)
            } else {
                throw error
            }
        }
    }

    private suspend fun getCachedAnimeList(
        cacheKey: String,
        fetchRemote: suspend () -> JikanAnimeListResponse
    ): JikanAnimeListResponse {
        val cached = runCatching { pageCacheLocalDataSource.getPage(cacheKey) }.getOrNull()

        if (cached?.isFresh() == true) {
            return JikanResponseCacheCodec.decodeAnimeList(cached.payloadJson)
        }

        if (!cachePolicy.allowsNetwork()) {
            return cached?.let { JikanResponseCacheCodec.decodeAnimeList(it.payloadJson) }
                ?: error(cachePolicy.cacheMissMessage("daftar anime"))
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

        if (!cachePolicy.allowsNetwork()) {
            return cached?.let { JikanResponseCacheCodec.decodeRecommendations(it.payloadJson) }
                ?: error(cachePolicy.cacheMissMessage("rekomendasi anime"))
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

    private suspend fun getCachedRandomAnimePicks(
        cacheKey: String,
        count: Int,
        forceRefresh: Boolean
    ): List<AnimeDetailData> {
        val cached = runCatching { pageCacheLocalDataSource.getPage(cacheKey) }.getOrNull()

        if (!forceRefresh && cached?.isFresh() == true) {
            return JikanResponseCacheCodec.decodeAnimeDetails(cached.payloadJson)
        }

        if (!cachePolicy.allowsNetwork()) {
            return cached?.let { JikanResponseCacheCodec.decodeAnimeDetails(it.payloadJson) }
                ?: error(cachePolicy.cacheMissMessage("random anime"))
        }

        return runCatching {
            buildList {
                repeat(count.coerceAtLeast(1)) { index ->
                    runCatching { remoteDataSource.fetchRandomAnime().data }
                        .getOrNull()
                        ?.let { detail -> add(detail) }

                    if (index != count - 1) {
                        delay(JIKAN_REQUEST_SPACING_MS)
                    }
                }
            }
                .distinctBy { detail -> detail.mal_id }
                .also { details ->
                    if (details.isNotEmpty()) {
                        runCatching {
                            pageCacheLocalDataSource.savePage(
                                cacheKey = cacheKey,
                                payloadJson = JikanResponseCacheCodec.encodeAnimeDetails(details)
                            )
                        }
                    }
                }
                .takeIf { details -> details.isNotEmpty() }
                ?: error("Gagal memuat random anime")
        }.getOrElse { error ->
            if (cached != null) {
                JikanResponseCacheCodec.decodeAnimeDetails(cached.payloadJson)
            } else {
                throw error
            }
        }
    }

    private suspend fun getCachedWatchEpisodes(
        cacheKey: String,
        fetchRemote: suspend () -> WatchEpisodesResponse
    ): WatchEpisodesResponse {
        val cached = runCatching { pageCacheLocalDataSource.getPage(cacheKey) }.getOrNull()

        if (cached?.isFresh() == true) {
            return JikanResponseCacheCodec.decodeWatchEpisodes(cached.payloadJson)
        }

        if (!cachePolicy.allowsNetwork()) {
            return cached?.let { JikanResponseCacheCodec.decodeWatchEpisodes(it.payloadJson) }
                ?: error(cachePolicy.cacheMissMessage("episode terbaru"))
        }

        return runCatching {
            fetchRemote().also { response ->
                runCatching {
                    pageCacheLocalDataSource.savePage(
                        cacheKey = cacheKey,
                        payloadJson = JikanResponseCacheCodec.encodeWatchEpisodes(response)
                    )
                }
            }
        }.getOrElse { error ->
            if (cached != null) {
                JikanResponseCacheCodec.decodeWatchEpisodes(cached.payloadJson)
            } else {
                throw error
            }
        }
    }

    override suspend fun getAnimeDetail(
        malId: Int,
        forceRefresh: Boolean
    ): AnimeDetailBundle = withContext(dispatchers.default) {
        val watchedNumbers = progressLocalDataSource.getWatchedEpisodeNumbers(malId)
        val markedNumbers = progressLocalDataSource.getMarkedEpisodeNumbers(malId)
        val cachedDetail = runCatching {
            detailCacheLocalDataSource.getAnimeDetail(malId)
        }.getOrNull()

        if (!forceRefresh && cachedDetail?.isFresh() == true) {
            return@withContext buildAnimeDetailBundle(
                animeDto = cachedDetail.detail,
                episodeDtos = cachedDetail.episodes,
                watchedNumbers = watchedNumbers,
                markedNumbers = markedNumbers,
                loadRelationPreviews = true
            )
        }

        if (!cachePolicy.allowsNetwork()) {
            if (cachedDetail != null) {
                return@withContext buildAnimeDetailBundle(
                    animeDto = cachedDetail.detail,
                    episodeDtos = cachedDetail.episodes,
                    watchedNumbers = watchedNumbers,
                    markedNumbers = markedNumbers,
                    loadRelationPreviews = true
                )
            }
            error(cachePolicy.cacheMissMessage("detail anime"))
        }

        runCatching {
            fetchRemoteAnimeDetailBundle(
                malId = malId,
                watchedNumbers = watchedNumbers,
                markedNumbers = markedNumbers
            )
        }.getOrElse { error ->
            if (cachedDetail != null) {
                buildAnimeDetailBundle(
                    animeDto = cachedDetail.detail,
                    episodeDtos = cachedDetail.episodes,
                    watchedNumbers = watchedNumbers,
                    markedNumbers = markedNumbers,
                    loadRelationPreviews = true
                )
            } else {
                throw error
            }
        }
    }

    private suspend fun fetchRemoteAnimeDetailBundle(
        malId: Int,
        watchedNumbers: Set<Int>,
        markedNumbers: Set<Int>
    ): AnimeDetailBundle {
        val animeDto = remoteDataSource.fetchAnimeFullDetail(malId).data
        val episodeDtos = runCatching {
            remoteDataSource.fetchAnimeEpisodes(malId).data
        }.getOrDefault(emptyList())

        runCatching {
            detailCacheLocalDataSource.saveAnimeDetail(
                detail = animeDto,
                episodes = episodeDtos
            )
        }

        return buildAnimeDetailBundle(
            animeDto = animeDto,
            episodeDtos = episodeDtos,
            watchedNumbers = watchedNumbers,
            markedNumbers = markedNumbers,
            loadRelationPreviews = true
        )
    }

    private suspend fun buildAnimeDetailBundle(
        animeDto: AnimeDetailData,
        episodeDtos: List<AnimeEpisodeDto>,
        watchedNumbers: Set<Int>,
        markedNumbers: Set<Int>,
        loadRelationPreviews: Boolean
    ): AnimeDetailBundle {
        val episodes = if (episodeDtos.isNotEmpty()) {
            episodeDtos.map { episode ->
                episode.toDomain(
                    watched = episode.mal_id in watchedNumbers,
                    marked = episode.mal_id in markedNumbers
                )
            }
        } else {
            (1..(animeDto.episodes ?: 0)).map { number ->
                AnimeEpisode(
                    number = number,
                    title = "Episode $number",
                    titleJapanese = null,
                    titleRomanji = null,
                    aired = null,
                    filler = false,
                    recap = false,
                    watched = number in watchedNumbers,
                    marked = number in markedNumbers
                )
            }
        }

        val relationPreviews = if (loadRelationPreviews) {
            fetchRelationPreviews(entries = animeDto.relations.flatMap { it.entry })
        } else {
            emptyMap()
        }

        return AnimeDetailBundle(
            anime = animeDto.toDomain(relationPreviews),
            episodes = episodes
        )
    }

    override suspend fun setEpisodeWatched(
        animeId: Int,
        episodeNumber: Int,
        watched: Boolean
    ) {
        withContext(dispatchers.default) {
            progressLocalDataSource.setEpisodeWatched(
                animeId = animeId,
                episodeNumber = episodeNumber,
                watched = watched
            )
        }
    }


    override suspend fun setEpisodeMarked(
        animeId: Int,
        episodeNumber: Int,
        marked: Boolean
    ) {
        withContext(dispatchers.default) {
            progressLocalDataSource.setEpisodeMarked(
                animeId = animeId,
                episodeNumber = episodeNumber,
                marked = marked
            )
        }
    }

    private suspend fun fetchRelationPreviews(
        entries: List<AnimeRelationEntryDto>
    ): Map<String, AnimeRelationPreview> {
        val previews = mutableMapOf<String, AnimeRelationPreview>()

        entries
            .distinctBy { it.previewKey() }
            .forEachIndexed { index, entry ->
                val key = entry.previewKey()
                memoryRelationPreviewCache[key]?.let { cachedPreview ->
                    previews[key] = cachedPreview
                    return@forEachIndexed
                }

                val cachedPreview = runCatching {
                    relationPreviewCacheLocalDataSource.getPreview(key)
                }.getOrNull()

                if (cachedPreview?.isFresh() == true) {
                    val preview = JikanResponseCacheCodec.decodeRelationPreview(cachedPreview.previewJson)
                        .toDomain()
                    memoryRelationPreviewCache[key] = preview
                    previews[key] = preview
                    return@forEachIndexed
                }

                if (!cachePolicy.allowsNetwork()) {
                    if (cachedPreview != null) {
                        val preview = JikanResponseCacheCodec.decodeRelationPreview(cachedPreview.previewJson)
                            .toDomain()
                        memoryRelationPreviewCache[key] = preview
                        previews[key] = preview
                    }
                    return@forEachIndexed
                }

                if (index > 0) delay(JIKAN_REQUEST_SPACING_MS)

                val remotePreview = runCatching {
                    remoteDataSource.fetchRelationEntryPreview(
                        id = entry.mal_id,
                        type = entry.type
                    ).data
                }.getOrNull()

                if (remotePreview != null) {
                    val preview = remotePreview.toDomain()
                    runCatching {
                        relationPreviewCacheLocalDataSource.savePreview(
                            cacheKey = key,
                            previewJson = JikanResponseCacheCodec.encodeRelationPreview(remotePreview)
                        )
                    }
                    memoryRelationPreviewCache[key] = preview
                    previews[key] = preview
                    return@forEachIndexed
                }

                if (cachedPreview != null) {
                    val preview = JikanResponseCacheCodec.decodeRelationPreview(cachedPreview.previewJson)
                        .toDomain()
                    memoryRelationPreviewCache[key] = preview
                    previews[key] = preview
                }
            }

        return previews
    }
}