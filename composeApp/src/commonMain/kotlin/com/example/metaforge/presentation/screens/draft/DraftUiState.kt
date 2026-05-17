package com.example.metaforge.presentation.screens.draft

import com.example.metaforge.domain.model.DraftState

sealed interface DraftUiState {
    data object Loading : DraftUiState
    data class Ready(
        val draftState: DraftState,
        val isAnalyzing: Boolean = false
    ) : DraftUiState
    data class Error(val message: String) : DraftUiState
}