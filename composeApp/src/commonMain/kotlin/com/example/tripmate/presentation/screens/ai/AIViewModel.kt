package com.example.tripmate.presentation.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripmate.data.repository.AIRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AIUiState {
    data object Idle : AIUiState
    data object Loading : AIUiState
    data class Success(val result: String) : AIUiState
    data class Error(val message: String) : AIUiState
}

class AIViewModel(
    private val aiRepository: AIRepositoryImpl
) : ViewModel() {

    private val _uiState = MutableStateFlow<AIUiState>(AIUiState.Idle)
    val uiState: StateFlow<AIUiState> = _uiState.asStateFlow()

    private val _destination = MutableStateFlow("")
    val destination: StateFlow<String> = _destination.asStateFlow()

    private val _duration = MutableStateFlow("3")
    val duration: StateFlow<String> = _duration.asStateFlow()

    private val _budget = MutableStateFlow("")
    val budget: StateFlow<String> = _budget.asStateFlow()

    private val _interests = MutableStateFlow("")
    val interests: StateFlow<String> = _interests.asStateFlow()

    fun onDestinationChange(value: String) { _destination.value = value }
    fun onDurationChange(value: String) { _duration.value = value }
    fun onBudgetChange(value: String) { _budget.value = value }
    fun onInterestsChange(value: String) { _interests.value = value }

    fun generateItinerary() {
        val dest = _destination.value.trim()
        val dur = _duration.value.trim().toIntOrNull() ?: 3
        val bud = _budget.value.trim().toDoubleOrNull() ?: 0.0
        val inter = _interests.value.trim().ifEmpty { "wisata umum" }

        if (dest.isBlank()) {
            _uiState.value = AIUiState.Error("Destinasi tidak boleh kosong")
            return
        }

        viewModelScope.launch {
            _uiState.value = AIUiState.Loading
            aiRepository.generateItinerary(dest, dur, bud, inter)
                .onSuccess { _uiState.value = AIUiState.Success(it) }
                .onFailure { _uiState.value = AIUiState.Error(it.message ?: "Terjadi kesalahan") }
        }
    }

    fun reset() {
        _uiState.value = AIUiState.Idle
    }
}
