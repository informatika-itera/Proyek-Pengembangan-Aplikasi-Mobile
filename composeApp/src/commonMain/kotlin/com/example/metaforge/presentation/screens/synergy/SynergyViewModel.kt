package com.example.metaforge.presentation.screens.synergy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.metaforge.domain.repository.DraftRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SynergyViewModel(
    private val draftRepository: DraftRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<SynergyUiState>(SynergyUiState.Loading)
    val uiState: StateFlow<SynergyUiState> = _uiState.asStateFlow()

    init { loadDraftAndAnalyze() }

    private fun loadDraftAndAnalyze() {
        viewModelScope.launch {
            val draftState = draftRepository.getDraftState().first()
            // Sprint 3: akan diganti dengan Gemini API call
            // Untuk Sprint 2, tampilkan draft state saja
            _uiState.value = SynergyUiState.Analyzing(draftState)
        }
    }

    fun retry() { loadDraftAndAnalyze() }
}