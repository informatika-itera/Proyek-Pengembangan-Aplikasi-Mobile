package com.example.mybawanggacha.presentation.screens.manga.list

import com.example.mybawanggacha.domain.manga.model.MangaSummary

sealed interface MangaListUiState {
    data object Loading : MangaListUiState

    data class Success(
        val title: String,
        val subtitle: String,
        val manga: List<MangaSummary>,
        val canLoadMore: Boolean = false,
        val isLoadingMore: Boolean = false,
        val isRefreshing: Boolean = false
    ) : MangaListUiState

    data class Error(val message: String) : MangaListUiState
}
