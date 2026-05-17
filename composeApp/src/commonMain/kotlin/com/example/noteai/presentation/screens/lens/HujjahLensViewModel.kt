package com.example.noteai.presentation.screens.lens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteai.domain.model.islamic.TopicOption
import com.example.noteai.domain.repository.hujjah.HujjahRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HujjahLensUiState(
    val inputText: String = "",
    val selectedTopicId: String = "",
    val topics: List<TopicOption> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class HujjahLensViewModel(
    private val hujjahRepository: HujjahRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HujjahLensUiState())
    val uiState: StateFlow<HujjahLensUiState> = _uiState.asStateFlow()

    init {
        loadTopics()
    }

    private fun loadTopics() {
        viewModelScope.launch {
            try {
                hujjahRepository.getTopics().collect { topics ->
                    _uiState.value = _uiState.value.copy(
                        topics = topics,
                        selectedTopicId = _uiState.value.selectedTopicId.ifBlank {
                            topics.firstOrNull()?.id.orEmpty()
                        },
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Gagal memuat topik"
                )
            }
        }
    }

    fun onInputTextChanged(value: String) {
        _uiState.value = _uiState.value.copy(inputText = value)
    }

    fun onTopicSelected(topicId: String) {
        _uiState.value = _uiState.value.copy(selectedTopicId = topicId)
    }

    fun selectedTopicId(): String {
        return _uiState.value.selectedTopicId.ifBlank { "calm" }
    }
}
