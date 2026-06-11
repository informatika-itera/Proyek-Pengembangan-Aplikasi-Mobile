package id.my.sinanonym.mybawanggacha.presentation.screens.manga.list

import id.my.sinanonym.mybawanggacha.domain.manga.model.MangaSummary

sealed interface MangaListUiState {
    data object Loading : MangaListUiState

    data class Success(
        val title: String,
        val manga: List<MangaSummary>,
        val canLoadMore: Boolean = false,
        val isLoadingMore: Boolean = false,
        val isRefreshing: Boolean = false
    ) : MangaListUiState

    data class Error(val message: String) : MangaListUiState
}
