package com.example.edumate.presentation.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edumate.domain.repository.AIRepository
import com.example.edumate.domain.repository.WritingStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AIAssistantState(
    val isLoading: Boolean = false,
    val result: String? = null,
    val ideas: List<String> = emptyList(),
    val error: String? = null,
    val mode: AIWorkMode = AIWorkMode.IDEAS
)

enum class AIWorkMode(val title: String) {
    IDEAS("Ide Tugas"),
    SUMMARY("Ringkasan"),
    IMPROVE("Perbaiki Tulisan")
}

class AIAssistantViewModel(
    private val aiRepository: AIRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AIAssistantState())
    val uiState: StateFlow<AIAssistantState> = _uiState.asStateFlow()

    fun setMode(mode: AIWorkMode) {
        _uiState.update { it.copy(mode = mode, error = null, result = null, ideas = emptyList()) }
    }

    fun summarizeNote(content: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, result = null, ideas = emptyList()) }
            aiRepository.summarize(content).fold(
                onSuccess = { result ->
                    _uiState.update { it.copy(isLoading = false, result = result) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    fun improveWriting(content: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, result = null, ideas = emptyList()) }
            aiRepository.improveWriting(content, WritingStyle.ACADEMIC).fold(
                onSuccess = { result ->
                    _uiState.update { it.copy(isLoading = false, result = result) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    fun generateIdeas(topic: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, result = null, ideas = emptyList()) }
            aiRepository.generateIdeas(topic).fold(
                onSuccess = { result ->
                    _uiState.update { it.copy(isLoading = false, ideas = result) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    fun clearState() {
        _uiState.value = AIAssistantState()
    }
}
