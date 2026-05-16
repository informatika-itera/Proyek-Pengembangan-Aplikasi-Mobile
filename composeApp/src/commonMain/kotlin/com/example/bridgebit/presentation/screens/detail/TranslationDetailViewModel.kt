package com.example.bridgebit.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bridgebit.domain.model.Translation
import com.example.bridgebit.domain.repository.TranslationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface DetailUiState {
    object Loading : DetailUiState
    data class Success(val translation: Translation) : DetailUiState
    data class Error(val message: String) : DetailUiState
}

class TranslationDetailViewModel(
    private val repository: TranslationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadTranslationDetails(id: Long) {
        viewModelScope.launch {
            repository.getTranslationById(id).collect { translation ->
                if (translation != null) {
                    _uiState.value = DetailUiState.Success(translation)
                } else {
                    _uiState.value = DetailUiState.Error("Data terjemahan tidak ditemukan")
                }
            }
        }
    }
}