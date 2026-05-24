package id.pusakakata.ui.screens.detail

import id.pusakakata.domain.model.Word

sealed interface DetailUiState {
    object Loading : DetailUiState
    data class Success(val word: Word) : DetailUiState
    data class Error(val message: String) : DetailUiState
}
