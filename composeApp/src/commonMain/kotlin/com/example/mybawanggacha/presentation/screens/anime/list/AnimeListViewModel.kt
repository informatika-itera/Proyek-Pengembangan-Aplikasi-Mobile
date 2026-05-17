package com.example.mybawanggacha.presentation.screens.anime.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybawanggacha.domain.anime.model.AnimePage
import com.example.mybawanggacha.domain.anime.model.AnimeSeason
import com.example.mybawanggacha.domain.anime.model.AnimeSeasonPeriod
import com.example.mybawanggacha.domain.anime.repository.AnimeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class AnimeListViewModel(
    private val animeRepository: AnimeRepository
) : ViewModel() {

    private val currentSeasonPeriod = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .let { dateTime ->
            AnimeSeasonPeriod(
                year = dateTime.year,
                season = AnimeSeason.fromMonth(dateTime.monthNumber)
            )
        }

    private val fallbackPreviousSeasonPeriod = currentSeasonPeriod.previous()
    private val cache = AnimeListPageCache()
    private var loadJob: Job? = null
    private var loadMoreJob: Job? = null

    private val _selectedTab = MutableStateFlow(AnimeListTab.CurrentSeason)
    val selectedTab: StateFlow<AnimeListTab> = _selectedTab.asStateFlow()

    private val _seasonPeriods = MutableStateFlow(listOf(fallbackPreviousSeasonPeriod))
    val seasonPeriods: StateFlow<List<AnimeSeasonPeriod>> = _seasonPeriods.asStateFlow()

    private val _selectedSeasonPeriod = MutableStateFlow(fallbackPreviousSeasonPeriod)
    val selectedSeasonPeriod: StateFlow<AnimeSeasonPeriod> = _selectedSeasonPeriod.asStateFlow()

    private val _uiState = MutableStateFlow<AnimeListUiState>(AnimeListUiState.Loading)
    val uiState: StateFlow<AnimeListUiState> = _uiState.asStateFlow()

    init {
        loadSeasonPeriods()
        refresh()
    }

    fun selectTab(tab: AnimeListTab) {
        if (_selectedTab.value == tab) return
        _selectedTab.value = tab
        load(tab = tab, forceRefresh = false)
    }

    fun selectSeasonPeriod(period: AnimeSeasonPeriod) {
        _selectedSeasonPeriod.value = period
        if (_selectedTab.value != AnimeListTab.SeasonArchive) {
            _selectedTab.value = AnimeListTab.SeasonArchive
        }
        load(tab = AnimeListTab.SeasonArchive, forceRefresh = false)
    }

    fun refresh() {
        load(tab = _selectedTab.value, forceRefresh = true)
    }

    fun loadNextPage() {
        val currentState = _uiState.value as? AnimeListUiState.Success ?: return
        if (!currentState.canLoadMore || currentState.isLoadingMore) return

        val tab = _selectedTab.value
        val key = cacheKey(tab)
        val cachedEntry = cache.get(key) ?: return
        val nextPage = cachedEntry.nextPage ?: return

        loadMoreJob?.cancel()
        loadMoreJob = viewModelScope.launch {
            _uiState.value = currentState.copy(isLoadingMore = true)

            runCatching {
                fetchAnimePage(tab = tab, page = nextPage)
            }.onSuccess { page ->
                val mergedAnime = (cachedEntry.anime + page.items).distinctBy { it.malId }
                val updatedEntry = AnimeListCacheEntry(
                    anime = mergedAnime,
                    nextPage = page.nextPage,
                    canLoadMore = page.hasNextPage
                )

                cache.put(key, updatedEntry)
                showSuccess(tab = tab, entry = updatedEntry)
            }.onFailure {
                _uiState.value = currentState.copy(isLoadingMore = false)
            }
        }
    }

    private fun loadSeasonPeriods() {
        viewModelScope.launch {
            runCatching {
                animeRepository.getAvailableSeasonPeriods()
            }.onSuccess { periods ->
                val archivePeriods = periods
                    .filter { period -> period.isBefore(currentSeasonPeriod) }
                    .sortedByDescending { period -> period.sortValue }

                if (archivePeriods.isNotEmpty()) {
                    _seasonPeriods.value = archivePeriods
                    if (_selectedSeasonPeriod.value !in archivePeriods) {
                        _selectedSeasonPeriod.value = archivePeriods.first()
                    }
                }
            }
        }
    }

    private fun load(tab: AnimeListTab, forceRefresh: Boolean) {
        loadJob?.cancel()
        loadMoreJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = AnimeListUiState.Loading
            val key = cacheKey(tab)

            runCatching {
                if (!forceRefresh && cache.contains(key)) {
                    cache.get(key) ?: fetchAnimePage(tab = tab, page = FIRST_PAGE).toCacheEntry()
                } else {
                    fetchAnimePage(tab = tab, page = FIRST_PAGE).toCacheEntry()
                        .also { entry -> cache.put(key, entry) }
                }
            }.onSuccess { entry ->
                showSuccess(tab = tab, entry = entry)
            }.onFailure { error ->
                _uiState.value = AnimeListUiState.Error(
                    error.message ?: "Gagal memuat katalog anime"
                )
            }
        }
    }

    private suspend fun fetchAnimePage(tab: AnimeListTab, page: Int): AnimePage {
        return when (tab) {
            AnimeListTab.CurrentSeason -> animeRepository.getCurrentSeasonAnimePage(page = page)
            AnimeListTab.SeasonArchive -> animeRepository.getSeasonAnimePage(
                year = _selectedSeasonPeriod.value.year,
                season = _selectedSeasonPeriod.value.season,
                page = page
            )
            AnimeListTab.Upcoming -> animeRepository.getUpcomingSeasonAnimePage(page = page)
            AnimeListTab.TopAnime -> animeRepository.getTopAnimePage(page = page)
            AnimeListTab.Recommendations -> AnimePage(
                items = animeRepository.getRecommendations(),
                nextPage = null,
                hasNextPage = false
            )
        }
    }

    private fun showSuccess(tab: AnimeListTab, entry: AnimeListCacheEntry) {
        _uiState.value = AnimeListUiState.Success(
            title = tab.contentTitle(
                currentSeasonPeriod = currentSeasonPeriod,
                selectedSeasonPeriod = _selectedSeasonPeriod.value
            ),
            subtitle = tab.contentSubtitle(
                currentSeasonPeriod = currentSeasonPeriod,
                selectedSeasonPeriod = _selectedSeasonPeriod.value
            ),
            anime = entry.anime,
            canLoadMore = entry.canLoadMore,
            isLoadingMore = false
        )
    }

    private fun cacheKey(tab: AnimeListTab): String {
        return when (tab) {
            AnimeListTab.SeasonArchive -> _selectedSeasonPeriod.value.let { period ->
                "season:${period.year}:${period.season.apiKey}"
            }
            else -> tab.name
        }
    }

    private companion object {
        const val FIRST_PAGE = 1
    }
}