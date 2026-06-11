package id.my.sinanonym.mybawanggacha.presentation.screens.manga.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.my.sinanonym.mybawanggacha.domain.manga.model.MangaPage
import id.my.sinanonym.mybawanggacha.domain.manga.repository.MangaRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MangaListViewModel(
    private val mangaRepository: MangaRepository
) : ViewModel() {

    private val cache = MangaListPageCache()
    private var loadJob: Job? = null
    private var loadMoreJob: Job? = null

    private val _selectedTab = MutableStateFlow(MangaListTab.TopManga)
    val selectedTab: StateFlow<MangaListTab> = _selectedTab.asStateFlow()

    private val _uiState = MutableStateFlow<MangaListUiState>(MangaListUiState.Loading)
    val uiState: StateFlow<MangaListUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun selectTab(tab: MangaListTab) {
        if (_selectedTab.value == tab) return
        _selectedTab.value = tab
        load(tab = tab, forceRefresh = false)
    }

    fun refresh() {
        load(tab = _selectedTab.value, forceRefresh = true)
    }

    fun loadNextPage() {
        val currentState = _uiState.value as? MangaListUiState.Success ?: return
        if (!currentState.canLoadMore || currentState.isLoadingMore) return

        val tab = _selectedTab.value
        val cachedEntry = cache.get(tab.name) ?: return
        val nextPage = cachedEntry.nextPage ?: return

        loadMoreJob?.cancel()
        loadMoreJob = viewModelScope.launch {
            _uiState.value = currentState.copy(isLoadingMore = true)

            runCatching {
                fetchMangaPage(tab = tab, page = nextPage)
            }.onSuccess { page ->
                val mergedManga = (cachedEntry.manga + page.items).distinctBy { it.malId }
                val updatedEntry = MangaListCacheEntry(
                    manga = mergedManga,
                    nextPage = page.nextPage,
                    canLoadMore = page.hasNextPage
                )

                cache.put(tab.name, updatedEntry)
                showSuccess(tab = tab, entry = updatedEntry)
            }.onFailure {
                _uiState.value = currentState.copy(isLoadingMore = false)
            }
        }
    }

    private fun load(tab: MangaListTab, forceRefresh: Boolean) {
        loadJob?.cancel()
        loadMoreJob?.cancel()
        loadJob = viewModelScope.launch {
            val cachedEntry = if (!forceRefresh) cache.get(tab.name) else null
            if (cachedEntry != null) {
                showSuccess(tab = tab, entry = cachedEntry)
                return@launch
            }

            val previousState = _uiState.value as? MangaListUiState.Success
            val canKeepPreviousContent = forceRefresh && previousState != null
            if (canKeepPreviousContent) {
                _uiState.value = previousState.copy(
                    isRefreshing = true,
                    isLoadingMore = false
                )
            } else {
                _uiState.value = MangaListUiState.Loading
            }

            runCatching {
                fetchMangaPage(tab = tab, page = FIRST_PAGE).toCacheEntry()
                    .also { entry -> cache.put(tab.name, entry) }
            }.onSuccess { entry ->
                showSuccess(tab = tab, entry = entry)
            }.onFailure { error ->
                _uiState.value = if (canKeepPreviousContent) {
                    previousState.copy(
                        isRefreshing = false,
                        isLoadingMore = false
                    )
                } else {
                    MangaListUiState.Error(
                        error.message ?: "Gagal memuat katalog manga"
                    )
                }
            }
        }
    }

    private suspend fun fetchMangaPage(tab: MangaListTab, page: Int): MangaPage {
        return when (tab) {
            MangaListTab.TopManga -> mangaRepository.getTopMangaPage(page = page)
            MangaListTab.Popular -> mangaRepository.getPopularMangaPage(page = page)
            MangaListTab.Recommendations -> MangaPage(
                items = mangaRepository.getRecommendations(),
                nextPage = null,
                hasNextPage = false
            )
        }
    }

    private fun showSuccess(tab: MangaListTab, entry: MangaListCacheEntry) {
        _uiState.value = MangaListUiState.Success(
            title = tab.contentTitle(),
            manga = entry.manga,
            canLoadMore = entry.canLoadMore,
            isLoadingMore = false,
            isRefreshing = false
        )
    }

    private companion object {
        const val FIRST_PAGE = 1
    }
}
