package com.example.tripmate.presentation.screens.home

import com.example.tripmate.domain.model.Trip

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val trips: List<Trip>
    ) : HomeUiState
    data class Error(
        val message: String
    ) : HomeUiState
    data object Empty : HomeUiState
}
