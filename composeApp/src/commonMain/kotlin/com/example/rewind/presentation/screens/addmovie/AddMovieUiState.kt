package com.example.rewind.presentation.screens.addmovie

import com.example.rewind.domain.model.Movie

sealed interface AddMovieUiState {
    data object Idle : AddMovieUiState
    data object Loading : AddMovieUiState
    data object Success : AddMovieUiState
    data class Error(val message: String) : AddMovieUiState
    data class EditMode(val movie: Movie) : AddMovieUiState
}