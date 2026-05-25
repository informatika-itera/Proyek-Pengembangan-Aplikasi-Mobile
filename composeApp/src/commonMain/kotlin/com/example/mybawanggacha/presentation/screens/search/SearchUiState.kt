package com.example.mybawanggacha.presentation.screens.search

import com.example.mybawanggacha.domain.search.model.MediaSearchItem

sealed interface SearchUiState {
    data object Idle : SearchUiState
    data object Loading : SearchUiState
    data class Error(val message: String) : SearchUiState
    data class Success(
        val items: List<MediaSearchItem>,
        val nextPage: Int?,
        val isLoadingMore: Boolean = false
    ) : SearchUiState {
        val canLoadMore: Boolean
            get() = nextPage != null
    }
}
