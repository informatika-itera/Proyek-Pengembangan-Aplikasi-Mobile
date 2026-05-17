package com.example.metaforge.presentation.screens.draft

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.metaforge.domain.repository.DraftRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class DraftViewModel(
    private val draftRepository: DraftRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DraftUiState>(DraftUiState.Loading)
    val uiState: StateFlow<DraftUiState> = _uiState.asStateFlow()

    init { observeDraftState() }

    private fun observeDraftState() {
        viewModelScope.launch {
            draftRepository.getDraftState()
                .catch { e ->
                    _uiState.value = DraftUiState.Error(
                        e.message ?: "Terjadi kesalahan"
                    )
                }
                .collect { draftState ->
                    _uiState.value = DraftUiState.Ready(draftState)
                }
        }
    }

    fun removeHero(slotIndex: Int, isAlly: Boolean) {
        viewModelScope.launch {
            draftRepository.removeHero(slotIndex, isAlly)
        }
    }

    fun resetDraft() {
        viewModelScope.launch {
            draftRepository.resetDraft()
        }
    }

    fun saveDraft() {
        viewModelScope.launch {
            draftRepository.saveDraft()
        }
    }
}