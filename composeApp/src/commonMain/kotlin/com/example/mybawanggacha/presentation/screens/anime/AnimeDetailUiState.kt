package com.example.mybawanggacha.presentation.screens.anime

import com.example.mybawanggacha.domain.model.AnimeDetail
import com.example.mybawanggacha.domain.model.AnimeEpisode

sealed interface AnimeDetailUiState {
    data object Loading : AnimeDetailUiState
    data class Success(
        val anime: AnimeDetail,
        val episodes: List<AnimeEpisode> = emptyList()
    ) : AnimeDetailUiState
    data class Error(val message: String) : AnimeDetailUiState
}
