package id.my.sinanonym.mybawanggacha.domain.gacha.usecase

import id.my.sinanonym.mybawanggacha.domain.gacha.model.GachaMediaFormat
import id.my.sinanonym.mybawanggacha.domain.gacha.model.GachaMediaPool
import id.my.sinanonym.mybawanggacha.domain.gacha.model.GachaPreference
import id.my.sinanonym.mybawanggacha.domain.gacha.model.GachaStatusFilter
import id.my.sinanonym.mybawanggacha.domain.gacha.model.GachaHistoryEntry
import id.my.sinanonym.mybawanggacha.domain.gacha.repository.GachaRepository
import id.my.sinanonym.mybawanggacha.domain.library.model.LibraryEntry
import id.my.sinanonym.mybawanggacha.domain.library.model.LibraryStatus
import id.my.sinanonym.mybawanggacha.domain.library.model.MediaType
import id.my.sinanonym.mybawanggacha.domain.library.repository.LibraryRepository
import id.my.sinanonym.mybawanggacha.domain.search.model.MediaSearchFilters
import id.my.sinanonym.mybawanggacha.domain.search.model.MediaSearchItem
import id.my.sinanonym.mybawanggacha.domain.search.model.MediaSearchPage
import id.my.sinanonym.mybawanggacha.domain.search.model.SearchFilterMetadata
import id.my.sinanonym.mybawanggacha.domain.search.model.SearchMediaType
import id.my.sinanonym.mybawanggacha.domain.search.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RunGachaUseCaseTest {
    @Test
    fun invoke_withBothMedia_shouldSearchAnimeAndMangaWithMappedFilters() = runTest {
        val searchRepository = FakeSearchRepository(
            pages = mapOf(
                SearchMediaType.Anime to listOf(createItem(id = 1, mediaType = SearchMediaType.Anime, score = 8.1)),
                SearchMediaType.Manga to listOf(createItem(id = 2, mediaType = SearchMediaType.Manga, score = 8.4))
            )
        )
        val useCase = RunGachaUseCase(
            searchRepository = searchRepository,
            libraryRepository = FakeLibraryRepository(),
            gachaRepository = FakeGachaRepository()
        )

        val result = useCase(
            preference = GachaPreference(
                mediaPool = GachaMediaPool.Both,
                selectedGenreIds = listOf(1, 2),
                excludedGenreIds = listOf(9),
                allowNsfw = true,
                minScore = "8",
                status = GachaStatusFilter.Completed,
                format = GachaMediaFormat.Any,
                includeKnownItems = true
            ),
            random = Random(1)
        )

        assertTrue(result.item.malId in listOf(1, 2))
        assertEquals(listOf(SearchMediaType.Anime, SearchMediaType.Manga), searchRepository.requests.map { it.mediaType })
        assertTrue(searchRepository.requests.all { it.genres == "1,2" })
        assertTrue(searchRepository.requests.all { it.genresExclude == "9" })
        assertTrue(searchRepository.requests.all { !it.sfw })
        assertTrue(searchRepository.requests.all { it.minScore == "8" })
        assertTrue(searchRepository.requests.all { it.status == "complete" })
    }

    @Test
    fun invoke_whenKnownItemsAreExcluded_shouldSkipLibraryEntries() = runTest {
        val searchRepository = FakeSearchRepository(
            pages = mapOf(
                SearchMediaType.Anime to listOf(
                    createItem(id = 1, mediaType = SearchMediaType.Anime, score = 8.0),
                    createItem(id = 2, mediaType = SearchMediaType.Anime, score = 8.2)
                )
            )
        )
        val useCase = RunGachaUseCase(
            searchRepository = searchRepository,
            libraryRepository = FakeLibraryRepository(
                entries = listOf(
                    LibraryEntry(
                        mediaId = 1,
                        mediaType = MediaType.Anime,
                        title = "Known",
                        status = LibraryStatus.Completed
                    )
                )
            ),
            gachaRepository = FakeGachaRepository()
        )

        val result = useCase(
            preference = GachaPreference(
                mediaPool = GachaMediaPool.Anime,
                includeKnownItems = false
            ),
            random = Random(1)
        )

        assertEquals(2, result.item.malId)
    }


    @Test
    fun invoke_withSamePreference_shouldDrawFromDeckWithoutRepeating() = runTest {
        val searchRepository = FakeSearchRepository(
            pages = mapOf(
                SearchMediaType.Anime to listOf(
                    createItem(id = 1, mediaType = SearchMediaType.Anime, score = 8.0),
                    createItem(id = 2, mediaType = SearchMediaType.Anime, score = 8.1),
                    createItem(id = 3, mediaType = SearchMediaType.Anime, score = 8.2)
                )
            )
        )
        val useCase = RunGachaUseCase(
            searchRepository = searchRepository,
            libraryRepository = FakeLibraryRepository(),
            gachaRepository = FakeGachaRepository()
        )
        val preference = GachaPreference(mediaPool = GachaMediaPool.Anime)

        val first = useCase(preference = preference, random = Random(1)).item
        val second = useCase(preference = preference, random = Random(1)).item
        val third = useCase(preference = preference, random = Random(1)).item

        assertEquals(3, setOf(first.malId, second.malId, third.malId).size)
        assertEquals(1, searchRepository.requests.size)
    }

    private fun createItem(
        id: Int,
        mediaType: SearchMediaType,
        score: Double?
    ): MediaSearchItem {
        return MediaSearchItem(
            malId = id,
            mediaType = mediaType,
            title = "Item $id",
            imageUrl = null,
            type = null,
            status = null,
            score = score,
            rank = null,
            episodes = null,
            chapters = null,
            volumes = null
        )
    }
}

private class FakeSearchRepository(
    private val pages: Map<SearchMediaType, List<MediaSearchItem>>
) : SearchRepository {
    val requests = mutableListOf<MediaSearchFilters>()

    override suspend fun search(
        filters: MediaSearchFilters,
        page: Int
    ): MediaSearchPage {
        requests += filters
        return MediaSearchPage(
            items = pages[filters.mediaType].orEmpty(),
            nextPage = null,
            hasNextPage = false
        )
    }

    override suspend fun getFilterMetadata(mediaType: SearchMediaType): SearchFilterMetadata {
        return SearchFilterMetadata()
    }
}


private class FakeGachaRepository(
    private val preference: GachaPreference = GachaPreference(),
    private val history: List<GachaHistoryEntry> = emptyList()
) : GachaRepository {
    override fun observeLastPreference(): Flow<GachaPreference> = flowOf(preference)

    override fun observeHistory(): Flow<List<GachaHistoryEntry>> = flowOf(history)

    override suspend fun getLastPreference(): GachaPreference = preference

    override suspend fun saveLastPreference(preference: GachaPreference) = Unit

    override suspend fun saveHistoryEntry(entry: GachaHistoryEntry) = Unit

    override suspend fun clearHistory() = Unit
}

private class FakeLibraryRepository(
    private val entries: List<LibraryEntry> = emptyList()
) : LibraryRepository {
    override fun observeEntries(): Flow<List<LibraryEntry>> = flowOf(entries)

    override fun observeEntriesByStatus(status: LibraryStatus): Flow<List<LibraryEntry>> {
        return flowOf(entries.filter { entry -> entry.status == status })
    }

    override fun observeEntry(mediaId: Int, mediaType: MediaType): Flow<LibraryEntry?> {
        return flowOf(entries.firstOrNull { entry ->
            entry.mediaId == mediaId && entry.mediaType == mediaType
        })
    }

    override suspend fun getEntries(): List<LibraryEntry> = entries

    override suspend fun getEntryById(id: Long): LibraryEntry? {
        return entries.firstOrNull { entry -> entry.id == id }
    }

    override suspend fun getEntry(mediaId: Int, mediaType: MediaType): LibraryEntry? {
        return entries.firstOrNull { entry ->
            entry.mediaId == mediaId && entry.mediaType == mediaType
        }
    }

    override suspend fun upsertEntry(entry: LibraryEntry): Long = entry.id

    override suspend fun deleteEntry(id: Long) = Unit

    override suspend fun deleteEntry(mediaId: Int, mediaType: MediaType) = Unit
}
