package com.example.rewind.presentation.screens.detail

import com.example.rewind.domain.model.Movie

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data object NotFound : DetailUiState
    data class Success(val movie: Movie) : DetailUiState
}