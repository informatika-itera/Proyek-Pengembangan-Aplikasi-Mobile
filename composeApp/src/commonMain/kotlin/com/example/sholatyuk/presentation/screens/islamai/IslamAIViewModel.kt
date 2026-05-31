package com.example.sholatyuk.presentation.screens.islamai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sholatyuk.domain.model.ChatMessage
import com.example.sholatyuk.domain.model.MessageRole
import com.example.sholatyuk.domain.repository.AIRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class IslamAIViewModel(
    private val aiRepository: AIRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IslamAIUiState())
    val uiState: StateFlow<IslamAIUiState> = _uiState.asStateFlow()

    init {
        loadChatHistory()
    }

    private fun loadChatHistory() {
        viewModelScope.launch {
            aiRepository.getChatHistory().collect { history ->
                if (history.isEmpty()) {
                    val welcomeMsg = ChatMessage(
                        id = 0L,
                        content = "Assalamu'alaikum. Saya IslamAI, asisten virtual yang siap membantu Anda menjawab pertanyaan seputar agama Islam. Ada yang bisa saya bantu?",
                        role = MessageRole.ASSISTANT,
                        timestamp = Clock.System.now(),
                        isError = false
                    )
                    _uiState.update { it.copy(messages = listOf(welcomeMsg)) }
                } else {
                    _uiState.update { it.copy(messages = history) }
                }
            }
        }
    }

    fun sendMessage(messageText: String) {
        if (messageText.isBlank()) return

        viewModelScope.launch {
            val userMsg = ChatMessage(
                id = 0L,
                content = messageText,
                role = MessageRole.USER,
                timestamp = Clock.System.now(),
                isError = false
            )

            _uiState.update { state ->
                state.copy(
                    messages = state.messages + userMsg,
                    isLoading = true,
                    error = null
                )
            }

            aiRepository.saveMessage(userMsg)

            aiRepository.askIslamAI(messageText).fold(
                onSuccess = { answer ->
                    val aiMsg = ChatMessage(
                        id = 0L,
                        content = answer,
                        role = MessageRole.ASSISTANT,
                        timestamp = Clock.System.now(),
                        isError = false
                    )

                    aiRepository.saveMessage(aiMsg)

                    _uiState.update { state ->
                        state.copy(
                            messages = state.messages + aiMsg,
                            isLoading = false
                        )
                    }
                },
                onFailure = { exception ->
                    val errorMsg = ChatMessage(
                        id = 0L,
                        content = "Maaf, terjadi kesalahan: ${exception.message}",
                        role = MessageRole.ASSISTANT,
                        timestamp = Clock.System.now(),
                        isError = true
                    )

                    _uiState.update { state ->
                        state.copy(
                            messages = state.messages + errorMsg,
                            isLoading = false,
                            error = exception.message
                        )
                    }
                }
            )
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            aiRepository.clearHistory()
            _uiState.update { it.copy(messages = emptyList()) }
            loadChatHistory()
        }
    }
}