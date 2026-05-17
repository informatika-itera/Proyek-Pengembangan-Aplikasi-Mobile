package com.example.noteai.presentation.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteai.domain.repository.AIRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean
)

data class ChatUiState(
    val messages: List<ChatMessage> = listOf(
        ChatMessage("Halo! Mau masak apa hari ini?", false)
    ),
    val inputText: String = "",
    val isLoading: Boolean = false
)

class ChatViewModel(
    private val aiRepository: AIRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun onInputTextChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun sendMessage() {
        val messageText = _uiState.value.inputText
        if (messageText.isBlank()) return

        val userMessage = ChatMessage(messageText, true)
        _uiState.update { 
            it.copy(
                messages = it.messages + userMessage,
                inputText = "",
                isLoading = true
            )
        }

        viewModelScope.launch {
            val result = aiRepository.chat(messageText)
            result.onSuccess { aiResponse ->
                _uiState.update { 
                    it.copy(
                        messages = it.messages + ChatMessage(aiResponse, false),
                        isLoading = false
                    )
                }
            }.onFailure { error ->
                _uiState.update { 
                    it.copy(
                        messages = it.messages + ChatMessage("Maaf, terjadi kesalahan: ${error.message}", false),
                        isLoading = false
                    )
                }
            }
        }
    }
}
