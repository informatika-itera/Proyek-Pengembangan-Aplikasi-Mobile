package com.example.mybawanggacha.presentation.screens.anime.home

import com.example.mybawanggacha.domain.anime.model.AnimeSummary

sealed interface AnimeHomeUiState {
    data object Loading : AnimeHomeUiState
    data class Success(val recommendations: List<AnimeSummary>) : AnimeHomeUiState
    data class Error(val message: String) : AnimeHomeUiState
}
