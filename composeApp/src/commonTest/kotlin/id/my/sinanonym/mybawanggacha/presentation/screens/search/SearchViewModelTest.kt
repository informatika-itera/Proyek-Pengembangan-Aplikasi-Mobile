package id.my.sinanonym.mybawanggacha.presentation.screens.search

import id.my.sinanonym.mybawanggacha.domain.search.model.MediaSearchFilters
import id.my.sinanonym.mybawanggacha.domain.search.model.MediaSearchItem
import id.my.sinanonym.mybawanggacha.domain.search.model.MediaSearchPage
import id.my.sinanonym.mybawanggacha.domain.search.model.SearchMediaType
import id.my.sinanonym.mybawanggacha.domain.search.repository.SearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun submitSearch_whenRepositorySucceeds_shouldExposeSuccessState() = runTest {
        val repository = FakeSearchRepository()
        repository.pages[1] = MediaSearchPage(
            items = listOf(createItem(id = 1, title = "Result")),
            nextPage = null,
            hasNextPage = false
        )
        val viewModel = SearchViewModel(repository)

        viewModel.submitSearch()
        advanceUntilIdle()

        val state = assertIs<SearchUiState.Success>(viewModel.uiState.value)
        assertEquals(listOf("Result"), state.items.map { it.title })
        assertFalse(state.canLoadMore)
    }

    @Test
    fun submitSearch_whenRepositoryFails_shouldExposeErrorState() = runTest {
        val repository = FakeSearchRepository()
        repository.failures[1] = IllegalStateException("network down")
        val viewModel = SearchViewModel(repository)

        viewModel.submitSearch()
        advanceUntilIdle()

        val state = assertIs<SearchUiState.Error>(viewModel.uiState.value)
        assertEquals("network down", state.message)
    }

    @Test
    fun resetFilters_shouldCancelContentAndKeepCurrentMediaType() = runTest {
        val repository = FakeSearchRepository()
        repository.pages[1] = MediaSearchPage(
            items = listOf(createItem(id = 1, title = "Result")),
            nextPage = null,
            hasNextPage = false
        )
        val viewModel = SearchViewModel(repository)

        viewModel.updateFilters(MediaSearchFilters(mediaType = SearchMediaType.Manga, query = "one piece"))
        viewModel.submitSearch()
        advanceUntilIdle()
        viewModel.resetFilters()

        assertEquals(SearchMediaType.Manga, viewModel.filters.value.mediaType)
        assertEquals("", viewModel.filters.value.query)
        assertEquals(SearchUiState.Idle, viewModel.uiState.value)
        assertFalse(viewModel.isRefreshing.value)
    }

    @Test
    fun refresh_whenSuccessAlreadyExists_shouldKeepContentAndUseRefreshingFlag() = runTest {
        val repository = FakeSearchRepository()
        repository.pages[1] = MediaSearchPage(
            items = listOf(createItem(id = 1, title = "Old Result")),
            nextPage = 2,
            hasNextPage = true
        )
        val viewModel = SearchViewModel(repository)

        viewModel.submitSearch()
        advanceUntilIdle()

        val initialState = assertIs<SearchUiState.Success>(viewModel.uiState.value)
        assertEquals("Old Result", initialState.items.first().title)

        repository.pages[1] = MediaSearchPage(
            items = listOf(createItem(id = 2, title = "Fresh Result")),
            nextPage = null,
            hasNextPage = false
        )

        viewModel.refresh()

        assertTrue(viewModel.isRefreshing.value)
        val stateWhileRefreshing = assertIs<SearchUiState.Success>(viewModel.uiState.value)
        assertEquals("Old Result", stateWhileRefreshing.items.first().title)

        advanceUntilIdle()

        assertFalse(viewModel.isRefreshing.value)
        val refreshedState = assertIs<SearchUiState.Success>(viewModel.uiState.value)
        assertEquals("Fresh Result", refreshedState.items.first().title)
    }

    @Test
    fun refresh_whenRefreshFails_shouldKeepPreviousSuccessContent() = runTest {
        val repository = FakeSearchRepository()
        repository.pages[1] = MediaSearchPage(
            items = listOf(createItem(id = 1, title = "Cached Result")),
            nextPage = 2,
            hasNextPage = true
        )
        val viewModel = SearchViewModel(repository)

        viewModel.submitSearch()
        advanceUntilIdle()
        repository.pages.remove(1)
        repository.failures[1] = IllegalStateException("refresh failed")

        viewModel.refresh()
        advanceUntilIdle()

        assertFalse(viewModel.isRefreshing.value)
        val state = assertIs<SearchUiState.Success>(viewModel.uiState.value)
        assertEquals("Cached Result", state.items.first().title)
        assertFalse(state.isLoadingMore)
    }

    @Test
    fun loadNextPage_whenCalledTwiceBeforeCompletion_shouldRequestNextPageOnlyOnce() = runTest {
        val repository = FakeSearchRepository()
        repository.pages[1] = MediaSearchPage(
            items = listOf(createItem(id = 1, title = "Page 1")),
            nextPage = 2,
            hasNextPage = true
        )
        repository.pages[2] = MediaSearchPage(
            items = listOf(createItem(id = 2, title = "Page 2")),
            nextPage = 3,
            hasNextPage = true
        )
        val viewModel = SearchViewModel(repository)

        viewModel.submitSearch()
        advanceUntilIdle()

        viewModel.loadNextPage()
        viewModel.loadNextPage()
        advanceUntilIdle()

        assertEquals(listOf(1, 2), repository.requestedPages)
        val state = assertIs<SearchUiState.Success>(viewModel.uiState.value)
        assertEquals(listOf(1, 2), state.items.map { it.malId })
        assertFalse(state.isLoadingMore)
    }

    @Test
    fun loadNextPage_whenNextPageFails_shouldKeepExistingItems() = runTest {
        val repository = FakeSearchRepository()
        repository.pages[1] = MediaSearchPage(
            items = listOf(createItem(id = 1, title = "Page 1")),
            nextPage = 2,
            hasNextPage = true
        )
        repository.failures[2] = IllegalStateException("page failed")
        val viewModel = SearchViewModel(repository)

        viewModel.submitSearch()
        advanceUntilIdle()
        viewModel.loadNextPage()
        advanceUntilIdle()

        val state = assertIs<SearchUiState.Success>(viewModel.uiState.value)
        assertEquals(listOf(1), state.items.map { it.malId })
        assertFalse(state.isLoadingMore)
    }

    private class FakeSearchRepository : SearchRepository {
        val pages = mutableMapOf<Int, MediaSearchPage>()
        val failures = mutableMapOf<Int, Throwable>()
        val requestedPages = mutableListOf<Int>()

        override suspend fun search(
            filters: MediaSearchFilters,
            page: Int
        ): MediaSearchPage {
            requestedPages += page
            failures[page]?.let { throw it }
            return pages[page] ?: MediaSearchPage(
                items = emptyList(),
                nextPage = null,
                hasNextPage = false
            )
        }
    }

    private fun createItem(
        id: Int,
        title: String
    ): MediaSearchItem {
        return MediaSearchItem(
            malId = id,
            mediaType = SearchMediaType.Anime,
            title = title,
            imageUrl = null,
            type = "TV",
            status = "Airing",
            score = 8.0,
            rank = null,
            episodes = 12,
            chapters = null,
            volumes = null
        )
    }
}
