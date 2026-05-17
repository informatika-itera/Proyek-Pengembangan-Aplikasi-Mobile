package com.example.noteai.presentation.screens.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteai.domain.model.islamic.IslamicReference
import com.example.noteai.domain.repository.hujjah.HujjahRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface HujjahResultUiState {
    data object Loading : HujjahResultUiState
    data class Success(
        val topicTitle: String,
        val references: List<IslamicReference>
    ) : HujjahResultUiState
    data class Error(val message: String) : HujjahResultUiState
    data object Empty : HujjahResultUiState
}

class HujjahResultViewModel(
    private val topicId: String,
    private val hujjahRepository: HujjahRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HujjahResultUiState>(HujjahResultUiState.Loading)
    val uiState: StateFlow<HujjahResultUiState> = _uiState.asStateFlow()

    init {
        loadReferences()
    }

    fun loadReferences() {
        viewModelScope.launch {
            _uiState.value = HujjahResultUiState.Loading
            try {
                hujjahRepository.getReferencesByTopic(topicId).collect { references ->
                    _uiState.value = when {
                        references.isEmpty() -> HujjahResultUiState.Empty
                        else -> HujjahResultUiState.Success(
                            topicTitle = references.first().topicTitle,
                            references = references
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = HujjahResultUiState.Error(
                    e.message ?: "Gagal memuat hasil Hujjah"
                )
            }
        }
    }
}
