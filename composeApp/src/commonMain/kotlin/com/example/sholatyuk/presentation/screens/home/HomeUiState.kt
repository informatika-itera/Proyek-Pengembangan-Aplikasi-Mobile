package com.example.sholatyuk.presentation.screens.home

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data object Success : HomeUiState
    data class Error(val message: String) : HomeUiState
}
