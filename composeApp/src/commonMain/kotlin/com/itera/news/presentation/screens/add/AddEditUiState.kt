package com.itera.news.presentation.screens.add

sealed interface AddEditUiState {
    data object Idle : AddEditUiState
    data object Loading : AddEditUiState
    data object Success : AddEditUiState
    data class Error(val message: String) : AddEditUiState
}
