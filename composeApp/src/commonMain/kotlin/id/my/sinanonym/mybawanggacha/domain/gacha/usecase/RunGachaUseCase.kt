package id.my.sinanonym.mybawanggacha.domain.gacha.usecase

import id.my.sinanonym.mybawanggacha.domain.gacha.model.GachaPreference
import id.my.sinanonym.mybawanggacha.domain.gacha.model.GachaResultItem
import id.my.sinanonym.mybawanggacha.domain.gacha.model.GachaRunResult
import id.my.sinanonym.mybawanggacha.domain.gacha.model.toGachaResultItem
import id.my.sinanonym.mybawanggacha.domain.gacha.repository.GachaRepository
import id.my.sinanonym.mybawanggacha.domain.library.repository.LibraryRepository
import id.my.sinanonym.mybawanggacha.domain.search.model.MediaSearchFilters
import id.my.sinanonym.mybawanggacha.domain.search.model.MediaSearchItem
import id.my.sinanonym.mybawanggacha.domain.search.model.SearchMediaType
import id.my.sinanonym.mybawanggacha.domain.search.repository.SearchRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random

class RunGachaUseCase(
    private val searchRepository: SearchRepository,
    private val libraryRepository: LibraryRepository,
    private val gachaRepository: GachaRepository
) {
    private val sessionMutex = Mutex()
    private var session: GachaDeckSession? = null

    suspend operator fun invoke(
        preference: GachaPreference,
        random: Random = Random.Default
    ): GachaRunResult = sessionMutex.withLock {
        val targetMediaTypes = preference.targetMediaTypes()
        if (targetMediaTypes.isEmpty()) {
            error("Format yang dipilih tidak cocok dengan media pool.")
        }

        val fingerprint = preference.stableFingerprint()
        val knownKeys = preference.knownLibraryKeys()
        val recentKeys = recentResultKeys()

        var activeSession = session
            ?.takeIf { current -> current.fingerprint == fingerprint }
            ?.filterDeck(
                knownKeys = knownKeys,
                recentKeys = recentKeys
            )
            ?: GachaDeckSession.create(
                fingerprint = fingerprint,
                mediaTypes = targetMediaTypes
            )

        if (activeSession.pool.isEmpty()) {
            activeSession = activeSession.fetchMoreCandidates(
                preference = preference,
                pageCount = INITIAL_POOL_PAGE_COUNT,
                knownKeys = knownKeys,
                recentKeys = recentKeys,
                random = random
            )
        }

        if (activeSession.deck.isEmpty() && activeSession.hasMorePages) {
            activeSession = activeSession.fetchMoreCandidates(
                preference = preference,
                pageCount = PREFETCH_PAGE_COUNT,
                knownKeys = knownKeys,
                recentKeys = recentKeys,
                random = random
            )
        }

        activeSession.drawNext()?.let { draw ->
            session = draw.session
            return@withLock GachaRunResult(
                item = draw.item,
                remainingCandidateCount = draw.session.deck.size,
                shouldPrefetch = draw.session.shouldPrefetch
            )
        }

        if (activeSession.pool.isEmpty()) {
            error("Tidak ada kandidat gacha yang cocok. Coba longgarkan filter atau aktifkan NSFW jika perlu.")
        }

        val strictSession = activeSession.rebuildDeckFromPool(
            knownKeys = knownKeys,
            recentKeys = recentKeys,
            random = random
        )

        strictSession.drawNext()?.let { draw ->
            session = draw.session
            return@withLock GachaRunResult(
                item = draw.item,
                remainingCandidateCount = draw.session.deck.size,
                infoMessage = "Semua kandidat unik sudah dicoba. Deck diacak ulang dengan recent exclusion tetap aktif.",
                shouldPrefetch = false
            )
        }

        val relaxedSession = activeSession.rebuildDeckFromPool(
            knownKeys = knownKeys,
            recentKeys = emptySet(),
            random = random
        )

        val relaxedDraw = relaxedSession.drawNext()
            ?: error("Semua kandidat unik sudah dicoba dan tidak ada kandidat yang bisa dipakai lagi.")

        session = relaxedDraw.session
        GachaRunResult(
            item = relaxedDraw.item,
            remainingCandidateCount = relaxedDraw.session.deck.size,
            infoMessage = "Semua kandidat unik sudah dicoba. Recent exclusion dilonggarkan agar gacha tetap bisa jalan.",
            shouldPrefetch = false
        )
    }

    suspend fun prefetchNextPage(
        preference: GachaPreference,
        random: Random = Random.Default
    ) = sessionMutex.withLock {
        val fingerprint = preference.stableFingerprint()
        val activeSession = session
            ?.takeIf { current -> current.fingerprint == fingerprint }
            ?: return@withLock

        if (!activeSession.shouldPrefetch) return@withLock

        val knownKeys = preference.knownLibraryKeys()
        val recentKeys = recentResultKeys()

        session = activeSession
            .filterDeck(
                knownKeys = knownKeys,
                recentKeys = recentKeys
            )
            .fetchMoreCandidates(
                preference = preference,
                pageCount = PREFETCH_PAGE_COUNT,
                knownKeys = knownKeys,
                recentKeys = recentKeys,
                random = random
            )
    }

    private suspend fun GachaDeckSession.fetchMoreCandidates(
        preference: GachaPreference,
        pageCount: Int,
        knownKeys: Set<String>,
        recentKeys: Set<String>,
        random: Random
    ): GachaDeckSession {
        var updatedSession = this

        preference.targetMediaTypes().forEach { mediaType ->
            var loadedPages = 0

            while (loadedPages < pageCount && updatedSession.canLoad(mediaType)) {
                val page = updatedSession.nextPageByMediaType[mediaType] ?: 1
                val searchPage = searchRepository.search(
                    filters = preference.searchFiltersFor(mediaType),
                    page = page
                )

                updatedSession = updatedSession.addCandidates(
                    candidates = searchPage.items
                        .filterByMinimumScore(preference.minScore)
                        .map { item -> item.toGachaResultItem() },
                    knownKeys = knownKeys,
                    recentKeys = recentKeys,
                    random = random
                )

                updatedSession = if (searchPage.hasNextPage) {
                    updatedSession.copy(
                        nextPageByMediaType = updatedSession.nextPageByMediaType +
                            (mediaType to (searchPage.nextPage ?: page + 1))
                    )
                } else {
                    updatedSession.copy(
                        exhaustedMediaTypes = updatedSession.exhaustedMediaTypes + mediaType
                    )
                }

                loadedPages++
            }
        }

        return updatedSession
    }

    private fun GachaDeckSession.addCandidates(
        candidates: List<GachaResultItem>,
        knownKeys: Set<String>,
        recentKeys: Set<String>,
        random: Random
    ): GachaDeckSession {
        val existingKeys = pool.map { item -> item.gachaKey() }.toSet()
        val newCandidates = candidates
            .filterNot { item -> item.gachaKey() in existingKeys }
            .distinctBy { item -> item.gachaKey() }

        val currentDeckKeys = deck.map { item -> item.gachaKey() }.toSet()
        val newDeckItems = newCandidates
            .filterNot { item -> item.libraryKey() in knownKeys }
            .filterNot { item -> item.gachaKey() in recentKeys }
            .filterNot { item -> item.gachaKey() in currentDeckKeys }
            .shuffled(random)

        return copy(
            pool = pool + newCandidates,
            deck = deck + newDeckItems
        )
    }

    private fun GachaDeckSession.rebuildDeckFromPool(
        knownKeys: Set<String>,
        recentKeys: Set<String>,
        random: Random
    ): GachaDeckSession {
        val rebuiltDeck = pool
            .filterNot { item -> item.libraryKey() in knownKeys }
            .filterNot { item -> item.gachaKey() in recentKeys }
            .distinctBy { item -> item.gachaKey() }
            .shuffled(random)

        return copy(deck = rebuiltDeck)
    }

    private fun GachaDeckSession.filterDeck(
        knownKeys: Set<String>,
        recentKeys: Set<String>
    ): GachaDeckSession {
        return copy(
            deck = deck
                .filterNot { item -> item.libraryKey() in knownKeys }
                .filterNot { item -> item.gachaKey() in recentKeys }
        )
    }

    private fun GachaDeckSession.drawNext(): GachaDeckDraw? {
        val item = deck.firstOrNull() ?: return null
        return GachaDeckDraw(
            item = item,
            session = copy(deck = deck.drop(1))
        )
    }

    private suspend fun GachaPreference.knownLibraryKeys(): Set<String> {
        if (includeKnownItems) return emptySet()

        return libraryRepository.getEntries()
            .map { entry -> "${entry.mediaType.storageKey}:${entry.mediaId}" }
            .toSet()
    }

    private suspend fun recentResultKeys(): Set<String> {
        return gachaRepository.observeHistory()
            .first()
            .asSequence()
            .map { history -> history.item.gachaKey() }
            .distinct()
            .take(RECENT_EXCLUSION_WINDOW)
            .toSet()
    }

    private fun GachaPreference.searchFiltersFor(mediaType: SearchMediaType): MediaSearchFilters {
        return MediaSearchFilters(
            mediaType = mediaType,
            limit = SEARCH_LIMIT_PER_PAGE.toString(),
            minScore = minScore.trim(),
            status = status.searchValueFor(mediaType),
            type = format.searchValueFor(mediaType),
            sfw = !allowNsfw,
            genres = selectedGenreIds.stableIdQuery(),
            genresExclude = excludedGenreIds.stableIdQuery()
        )
    }

    private fun GachaPreference.targetMediaTypes(): List<SearchMediaType> {
        return mediaPool
            .searchMediaTypes()
            .filter { mediaType -> format.supports(mediaType) }
    }

    private fun GachaPreference.stableFingerprint(): String {
        return buildString {
            append("v2")
            append("|pool=").append(mediaPool.name)
            append("|types=").append(targetMediaTypes().joinToString(",") { type -> type.name })
            append("|genres=").append(selectedGenreIds.stableIdQuery())
            append("|exclude=").append(excludedGenreIds.stableIdQuery())
            append("|min=").append(minScore.stableScoreValue())
            append("|status=").append(status.name)
            append("|format=").append(format.name)
            append("|known=").append(includeKnownItems)
            append("|sfw=").append(!allowNsfw)
        }
    }

    private fun List<MediaSearchItem>.filterByMinimumScore(minScore: String): List<MediaSearchItem> {
        val minimum = minScore.trim().toDoubleOrNull() ?: return this
        return filter { item -> item.score != null && item.score >= minimum }
    }

    private fun List<Int>.stableIdQuery(): String {
        return distinct()
            .sorted()
            .joinToString(",")
    }

    private fun String.stableScoreValue(): String {
        val trimmed = trim()
        return trimmed.toDoubleOrNull()?.toString() ?: trimmed
    }

    private fun GachaResultItem.gachaKey(): String {
        return "${mediaType.name}:$malId"
    }

    private fun GachaResultItem.libraryKey(): String {
        return "${toLibraryMediaType().storageKey}:$malId"
    }

    private data class GachaDeckSession(
        val fingerprint: String,
        val pool: List<GachaResultItem>,
        val deck: List<GachaResultItem>,
        val nextPageByMediaType: Map<SearchMediaType, Int>,
        val exhaustedMediaTypes: Set<SearchMediaType>
    ) {
        val hasMorePages: Boolean
            get() = nextPageByMediaType.keys.any { mediaType ->
                mediaType !in exhaustedMediaTypes
            }

        val shouldPrefetch: Boolean
            get() = deck.size <= DECK_PREFETCH_THRESHOLD && hasMorePages

        fun canLoad(mediaType: SearchMediaType): Boolean {
            return mediaType in nextPageByMediaType && mediaType !in exhaustedMediaTypes
        }

        companion object {
            fun create(
                fingerprint: String,
                mediaTypes: List<SearchMediaType>
            ): GachaDeckSession {
                return GachaDeckSession(
                    fingerprint = fingerprint,
                    pool = emptyList(),
                    deck = emptyList(),
                    nextPageByMediaType = mediaTypes.associateWith { 1 },
                    exhaustedMediaTypes = emptySet()
                )
            }
        }
    }

    private data class GachaDeckDraw(
        val item: GachaResultItem,
        val session: GachaDeckSession
    )

    private companion object {
        const val SEARCH_LIMIT_PER_PAGE = 25
        const val INITIAL_POOL_PAGE_COUNT = 3
        const val PREFETCH_PAGE_COUNT = 1
        const val DECK_PREFETCH_THRESHOLD = 8
        const val RECENT_EXCLUSION_WINDOW = 12
    }
}
