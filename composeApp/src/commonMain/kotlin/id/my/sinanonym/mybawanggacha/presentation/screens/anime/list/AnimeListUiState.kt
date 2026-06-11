package id.my.sinanonym.mybawanggacha.presentation.screens.anime.list

import id.my.sinanonym.mybawanggacha.domain.anime.model.AnimeSummary

sealed interface AnimeListUiState {
    data object Loading : AnimeListUiState

    data class Success(
        val title: String,
        val anime: List<AnimeSummary>,
        val canLoadMore: Boolean = false,
        val isLoadingMore: Boolean = false,
        val isRefreshing: Boolean = false
    ) : AnimeListUiState

    data class Error(val message: String) : AnimeListUiState
}
