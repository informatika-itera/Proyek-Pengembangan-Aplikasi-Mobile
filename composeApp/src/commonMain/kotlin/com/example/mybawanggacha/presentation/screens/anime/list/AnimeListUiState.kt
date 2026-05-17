package com.example.mybawanggacha.presentation.screens.anime.list

import com.example.mybawanggacha.domain.anime.model.AnimeSummary

sealed interface AnimeListUiState {
    data object Loading : AnimeListUiState

    data class Success(
        val title: String,
        val subtitle: String,
        val anime: List<AnimeSummary>,
        val canLoadMore: Boolean = false,
        val isLoadingMore: Boolean = false
    ) : AnimeListUiState

    data class Error(val message: String) : AnimeListUiState
}
