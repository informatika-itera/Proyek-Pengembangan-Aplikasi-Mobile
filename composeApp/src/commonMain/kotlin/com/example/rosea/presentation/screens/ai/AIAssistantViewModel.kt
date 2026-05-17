package com.example.rosea.presentation.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rosea.domain.repository.AIRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AIAssistantViewModel(
    private val aiRepository: AIRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AIUiState>(AIUiState.Initial)
    val uiState: StateFlow<AIUiState> = _uiState.asStateFlow()

    fun sendMessage(message: String) {
        if (message.isBlank()) return

        viewModelScope.launch {
            _uiState.value = AIUiState.Loading
            try {
                // Gunakan fungsi .chat(message) sesuai dengan AIRepository Anda
                val response = aiRepository.chat(message)

                response.fold(
                    onSuccess = { _uiState.value = AIUiState.Success(it) },
                    onFailure = { _uiState.value = AIUiState.Error(it.message ?: "Terjadi kesalahan") }
                )
            } catch (e: Exception) {
                _uiState.value = AIUiState.Error(e.message ?: "Terjadi kesalahan yang tidak diketahui")
            }
        }
    }

    fun resetState() {
        _uiState.value = AIUiState.Initial
    }
}

sealed interface AIUiState {
    object Initial : AIUiState
    object Loading : AIUiState
    data class Success(val response: String) : AIUiState
    data class Error(val message: String) : AIUiState
}