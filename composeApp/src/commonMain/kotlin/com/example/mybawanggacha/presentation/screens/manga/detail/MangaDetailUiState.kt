package com.example.mybawanggacha.presentation.screens.manga.detail

import com.example.mybawanggacha.domain.manga.model.MangaDetail

sealed interface MangaDetailUiState {
    data object Loading : MangaDetailUiState
    data class Success(
        val manga: MangaDetail,
        val libraryEntryId: Long? = null,
        val isRefreshing: Boolean = false
    ) : MangaDetailUiState
    data class Error(val message: String) : MangaDetailUiState
}
