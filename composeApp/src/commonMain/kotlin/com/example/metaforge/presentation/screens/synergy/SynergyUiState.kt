package com.example.metaforge.presentation.screens.synergy

import com.example.metaforge.domain.model.DraftState
import com.example.metaforge.domain.model.SynergyResult

sealed interface SynergyUiState {
    data object Loading : SynergyUiState
    data class Analyzing(val draftState: DraftState) : SynergyUiState
    data class Success(
        val draftState: DraftState,
        val result: SynergyResult
    ) : SynergyUiState
    data class Error(
        val message: String,
        val draftState: DraftState? = null
    ) : SynergyUiState
}