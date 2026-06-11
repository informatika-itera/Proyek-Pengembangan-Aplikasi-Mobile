package id.pusakakata.presentation.screens.home

import id.pusakakata.domain.model.Word

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val words: List<Word>) : HomeUiState
    data class Error(val message: String) : HomeUiState
    object Empty : HomeUiState
}
