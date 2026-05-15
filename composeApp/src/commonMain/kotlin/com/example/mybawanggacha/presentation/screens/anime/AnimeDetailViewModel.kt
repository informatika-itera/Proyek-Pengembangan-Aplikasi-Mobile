package com.example.mybawanggacha.presentation.screens.anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybawanggacha.data.remote.api.JikanService
import com.example.mybawanggacha.data.remote.dto.AnimeDetailData
import com.example.mybawanggacha.data.remote.dto.AnimeEpisodeDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AnimeDetailUiState {
    data object Loading : AnimeDetailUiState
    data class Success(
        val anime: AnimeDetailData,
        val episodes: List<AnimeEpisodeDto> = emptyList()
    ) : AnimeDetailUiState
    data class Error(val message: String) : AnimeDetailUiState
}

class AnimeDetailViewModel(
    private val jikanService: JikanService
) : ViewModel() {

    private val _uiState = MutableStateFlow<AnimeDetailUiState>(AnimeDetailUiState.Loading)
    val uiState: StateFlow<AnimeDetailUiState> = _uiState.asStateFlow()

    fun fetchAnimeDetail(malId: Int) {
        viewModelScope.launch {
            _uiState.value = AnimeDetailUiState.Loading
            try {
                val anime = jikanService.fetchAnimeFullDetail(malId).data
                val episodes = runCatching {
                    jikanService.fetchAnimeEpisodes(malId).data
                }.getOrDefault(emptyList())

                _uiState.value = AnimeDetailUiState.Success(
                    anime = anime,
                    episodes = episodes
                )
            } catch (e: Exception) {
                _uiState.value = AnimeDetailUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}
