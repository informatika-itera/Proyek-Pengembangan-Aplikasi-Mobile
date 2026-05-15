package com.example.mybawanggacha.presentation.screens.anime

import com.example.mybawanggacha.domain.model.AnimeSummary

sealed interface AnimeHomeUiState {
    data object Loading : AnimeHomeUiState
    data class Success(val recommendations: List<AnimeSummary>) : AnimeHomeUiState
    data class Error(val message: String) : AnimeHomeUiState
}
