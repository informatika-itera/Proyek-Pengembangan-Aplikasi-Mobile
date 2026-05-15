package com.dailybliss.app.presentation.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailybliss.app.domain.repository.AIRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AIAssistantViewModel(
    private val aiRepository: AIRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AIAssistantUiState())
    val uiState = _uiState.asStateFlow()

    fun onInputChange(input: String) {
        _uiState.update { it.copy(input = input) }
    }

    fun onImageSelected(bytes: ByteArray?) {
        _uiState.update { it.copy(selectedImageBytes = bytes) }
    }

    fun sendMessage() {
        val currentInput = _uiState.value.input.trim()
        val currentImage = _uiState.value.selectedImageBytes
        
        if (currentInput.isBlank() && currentImage == null) return

        val userMessage = ChatMessage(
            role = "user",
            text = currentInput,
            imageBytes = currentImage
        )

        // Add user message and a placeholder model message for streaming
        val placeholderModelMessage = ChatMessage(role = "model", text = "")
        
        _uiState.update { state ->
            state.copy(
                messages = state.messages + userMessage + placeholderModelMessage,
                input = "",
                selectedImageBytes = null,
                isLoading = true,
                error = null
            )
        }

        viewModelScope.launch {
            try {
                // Send history excluding the placeholder
                val history = _uiState.value.messages.dropLast(1)
                
                // Using non-streaming chat for better coherence as requested
                val fullResponse = aiRepository.chat(history)
                
                _uiState.update { state ->
                    val updatedMessages = state.messages.toMutableList()
                    val lastIndex = updatedMessages.lastIndex
                    if (lastIndex >= 0) {
                        updatedMessages[lastIndex] = updatedMessages[lastIndex].copy(text = fullResponse)
                    }
                    state.copy(messages = updatedMessages, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Gagal terhubung ke AI: ${e.message}") }
            }
        }
    }
}

data class ChatMessage(
    val role: String,
    val text: String,
    val imageBytes: ByteArray? = null
)

data class AIAssistantUiState(
    val messages: List<ChatMessage> = emptyList(),
    val input: String = "",
    val selectedImageBytes: ByteArray? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
