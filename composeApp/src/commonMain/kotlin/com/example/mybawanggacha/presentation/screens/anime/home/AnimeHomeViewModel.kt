package com.example.mybawanggacha.presentation.screens.anime.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybawanggacha.domain.anime.model.AnimeSummary
import com.example.mybawanggacha.domain.anime.repository.AnimeRepository
import com.example.mybawanggacha.domain.manga.model.MangaSummary
import com.example.mybawanggacha.domain.manga.repository.MangaRepository
import kotlinx.coroutines.async
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

    init {
        refresh()
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

            val homeState = supervisorScope {
                val recommendations = async {
                    runCatching { animeRepository.getRecommendations() }
                        .getOrDefault(emptyList<AnimeSummary>())
                }
                val randomAnime = async {
                    runCatching { animeRepository.getRandomAnimePicks(count = RANDOM_ANIME_PICK_COUNT) }
                        .getOrDefault(emptyList<AnimeSummary>())
                }
                val randomManga = async {
                    runCatching { mangaRepository.getRandomMangaPicks(count = RANDOM_MANGA_PICK_COUNT) }
                        .getOrDefault(emptyList<MangaSummary>())
                }
                val recentEpisodes = async {
                    runCatching { animeRepository.getRecentEpisodes() }
                        .getOrDefault(emptyList())
                }

                AnimeHomeUiState.Success(
                    recommendations = recommendations.await(),
                    randomAnime = randomAnime.await(),
                    randomManga = randomManga.await(),
                    recentEpisodes = recentEpisodes.await()
                )
            }

            if (
                homeState.recommendations.isEmpty() &&
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

    private companion object {
        const val RANDOM_ANIME_PICK_COUNT = 4
        const val RANDOM_MANGA_PICK_COUNT = 4
    }
}