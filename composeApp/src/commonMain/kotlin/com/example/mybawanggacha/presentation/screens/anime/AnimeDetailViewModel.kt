package com.example.mybawanggacha.presentation.screens.anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybawanggacha.domain.repository.AnimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AnimeDetailViewModel(
    private val animeRepository: AnimeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AnimeDetailUiState>(AnimeDetailUiState.Loading)
    val uiState: StateFlow<AnimeDetailUiState> = _uiState.asStateFlow()

    fun fetchAnimeDetail(malId: Int) {
        viewModelScope.launch {
            _uiState.value = AnimeDetailUiState.Loading

            runCatching {
                animeRepository.getAnimeDetail(malId)
            }.onSuccess { detail ->
                _uiState.value = AnimeDetailUiState.Success(
                    anime = detail.anime,
                    episodes = detail.episodes
                )
            }.onFailure { error ->
                _uiState.value = AnimeDetailUiState.Error(
                    error.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun setEpisodeWatched(
        episodeNumber: Int,
        watched: Boolean
    ) {
        val currentState = _uiState.value as? AnimeDetailUiState.Success ?: return

        viewModelScope.launch {
            val animeId = currentState.anime.malId

            runCatching {
                animeRepository.setEpisodeWatched(
                    animeId = animeId,
                    episodeNumber = episodeNumber,
                    watched = watched
                )
            }.onSuccess {
                val latestState = _uiState.value as? AnimeDetailUiState.Success ?: return@onSuccess
                if (latestState.anime.malId != animeId) return@onSuccess

                _uiState.value = latestState.copy(
                    episodes = latestState.episodes.map { episode ->
                        if (episode.number == episodeNumber) {
                            episode.copy(watched = watched)
                        } else {
                            episode
                        }
                    }
                )
            }
        }
    }
}