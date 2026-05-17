package com.example.metaforge.presentation.screens.draft

import com.example.metaforge.domain.model.DraftState

data class DraftRecommendation(
    val heroName: String,
    val role: String,
    val reason: String,
    val warning: String?
)

sealed interface DraftUiState {
    data object Loading : DraftUiState
    data class Ready(
        val draftState: DraftState,
        val isUserTurn: Boolean = false,
        val recommendation: DraftRecommendation? = null,
        val turnMessage: String = ""
    ) : DraftUiState
    data class Error(val message: String) : DraftUiState
}