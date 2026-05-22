package com.example.rewind.presentation.screens.home

import com.example.rewind.domain.model.Movie
import com.example.rewind.domain.usecase.MovieSortBy

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data object Empty : HomeUiState
    data class Success(
        val movies: List<Movie>,
        val sortBy: MovieSortBy = MovieSortBy.UPDATED_DESC
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}