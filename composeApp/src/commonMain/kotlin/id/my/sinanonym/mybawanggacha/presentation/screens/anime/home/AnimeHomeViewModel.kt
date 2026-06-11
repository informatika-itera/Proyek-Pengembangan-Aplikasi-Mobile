package id.my.sinanonym.mybawanggacha.presentation.screens.anime.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.my.sinanonym.mybawanggacha.domain.anime.model.AnimeSummary
import id.my.sinanonym.mybawanggacha.domain.anime.repository.AnimeRepository
import id.my.sinanonym.mybawanggacha.domain.manga.model.MangaSummary
import id.my.sinanonym.mybawanggacha.domain.manga.repository.MangaRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class AnimeHomeViewModel(
    private val animeRepository: AnimeRepository,
    private val mangaRepository: MangaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AnimeHomeUiState>(AnimeHomeUiState.Loading)
    val uiState: StateFlow<AnimeHomeUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _isRandomPickRefreshing = MutableStateFlow(false)

    init {
        refresh()
        startRandomPickAutoRefresh()
    }

    fun refresh() {
        if (_isRefreshing.value) return

        viewModelScope.launch {
            val hasContent = _uiState.value is AnimeHomeUiState.Success
            if (hasContent) {
                _isRefreshing.value = true
            } else {
                _uiState.value = AnimeHomeUiState.Loading
            }

            val homeState = loadHomeState(forceRandomRefresh = hasContent)

            if (
                homeState.recommendations.isEmpty() &&
                homeState.mangaRecommendations.isEmpty() &&
                homeState.randomAnime.isEmpty() &&
                homeState.randomManga.isEmpty() &&
                homeState.recentEpisodes.isEmpty()
            ) {
                if (!hasContent) {
                    _uiState.value = AnimeHomeUiState.Error("Gagal memuat data discovery dari Jikan")
                }
            } else {
                _uiState.value = homeState
            }

            _isRefreshing.value = false
        }
    }

    private fun startRandomPickAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                delay(RANDOM_PICK_REFRESH_INTERVAL_MS)
                refreshRandomPicks()
            }
        }
    }

    private fun refreshRandomPicks() {
        if (_isRandomPickRefreshing.value) return

        val current = _uiState.value as? AnimeHomeUiState.Success ?: return

        viewModelScope.launch {
            _isRandomPickRefreshing.value = true

            val next = supervisorScope {
                val randomAnime = async {
                    runCatching {
                        animeRepository.getRandomAnimePicks(
                            count = RANDOM_ANIME_APPEND_COUNT,
                            forceRefresh = true
                        )
                    }.getOrDefault(emptyList<AnimeSummary>())
                }
                val randomManga = async {
                    runCatching {
                        mangaRepository.getRandomMangaPicks(
                            count = RANDOM_MANGA_APPEND_COUNT,
                            forceRefresh = true
                        )
                    }.getOrDefault(emptyList<MangaSummary>())
                }

                val freshAnime = randomAnime.await()
                val freshManga = randomManga.await()

                if (freshAnime.isEmpty() && freshManga.isEmpty()) {
                    current
                } else {
                    current.copy(
                        randomAnime = (freshAnime + current.randomAnime)
                            .distinctBy { anime -> anime.malId }
                            .take(RANDOM_PICK_POOL_LIMIT),
                        randomManga = (freshManga + current.randomManga)
                            .distinctBy { manga -> manga.malId }
                            .take(RANDOM_PICK_POOL_LIMIT)
                    )
                }
            }

            _uiState.value = next
            _isRandomPickRefreshing.value = false
        }
    }

    private suspend fun loadHomeState(forceRandomRefresh: Boolean): AnimeHomeUiState.Success {
        return supervisorScope {
            val recommendations = async {
                runCatching { animeRepository.getRecommendations() }
                    .getOrDefault(emptyList<AnimeSummary>())
            }
            val mangaRecommendations = async {
                runCatching { mangaRepository.getRecommendations() }
                    .getOrDefault(emptyList<MangaSummary>())
            }
            val randomAnime = async {
                runCatching {
                    animeRepository.getRandomAnimePicks(
                        count = RANDOM_ANIME_PICK_COUNT,
                        forceRefresh = forceRandomRefresh
                    )
                }.getOrDefault(emptyList<AnimeSummary>())
            }
            val randomManga = async {
                runCatching {
                    mangaRepository.getRandomMangaPicks(
                        count = RANDOM_MANGA_PICK_COUNT,
                        forceRefresh = forceRandomRefresh
                    )
                }.getOrDefault(emptyList<MangaSummary>())
            }
            val recentEpisodes = async {
                runCatching { animeRepository.getRecentEpisodes() }
                    .getOrDefault(emptyList())
            }

            AnimeHomeUiState.Success(
                recommendations = recommendations.await(),
                mangaRecommendations = mangaRecommendations.await(),
                randomAnime = randomAnime.await(),
                randomManga = randomManga.await(),
                recentEpisodes = recentEpisodes.await()
            )
        }
    }


    private companion object {
        const val RANDOM_ANIME_PICK_COUNT = 4
        const val RANDOM_MANGA_PICK_COUNT = 4
        const val RANDOM_ANIME_APPEND_COUNT = 2
        const val RANDOM_MANGA_APPEND_COUNT = 2
        const val RANDOM_PICK_POOL_LIMIT = 12
        const val RANDOM_PICK_REFRESH_INTERVAL_MS = 120_000L
    }
}