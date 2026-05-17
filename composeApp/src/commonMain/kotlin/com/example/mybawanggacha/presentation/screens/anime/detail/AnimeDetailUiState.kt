package com.example.mybawanggacha.presentation.screens.anime.detail

import com.example.mybawanggacha.domain.anime.model.AnimeDetail
import com.example.mybawanggacha.domain.anime.model.AnimeEpisode

sealed interface AnimeDetailUiState {
    data object Loading : AnimeDetailUiState
    data class Success(
        val anime: AnimeDetail,
        val episodes: List<AnimeEpisode> = emptyList(),
        val libraryEntryId: Long? = null
    ) : AnimeDetailUiState
    data class Error(val message: String) : AnimeDetailUiState
}
