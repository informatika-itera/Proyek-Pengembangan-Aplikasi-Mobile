package com.example.raillog.presentation.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raillog.data.local.datastore.UserPreferences
import com.example.raillog.data.remote.api.SystemPrompts
import com.example.raillog.domain.repository.AIRepository
import com.example.raillog.domain.repository.SupplyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class MessageRole { USER, ASSISTANT }

data class ChatMessage(
    val id: Long = System.currentTimeMillis(),
    val role: MessageRole,
    val content: String
)

data class AIAssistantUiState(
    val inputText: String = "",
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val inventoryContext: String = ""
) {
    val canSend: Boolean get() = inputText.isNotBlank() && !isLoading
}

class AIAssistantViewModel(
    private val aiRepository: AIRepository,
    private val supplyRepository: SupplyRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(AIAssistantUiState())
    val uiState: StateFlow<AIAssistantUiState> = _uiState.asStateFlow()
    
    private val activeUsername = userPreferences.activeUsername
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    init {
        observeInventory()
    }

    private fun observeInventory() {
        viewModelScope.launch {
            activeUsername.collect { username ->
                supplyRepository.getAllItems(username).collect { items ->
                    if (items.isNotEmpty()) {
                        val contextLines = items.take(20).joinToString("\n") { item ->
                            "- [${item.partCode}] ${item.name} | Qty: ${item.quantity} ${item.unit} | Status: ${item.status.name} | Priority: ${item.priority.name}"
                        }
                        val fullContext = buildString {
                            appendLine("=== KONTEKS INVENTARIS REAL-TIME (${items.size} item total) ===")
                            appendLine(contextLines)
                            if (items.size > 20) appendLine("... dan ${items.size - 20} item lainnya.")
                        }
                        _uiState.update { it.copy(inventoryContext = fullContext) }
                    } else {
                        _uiState.update { it.copy(inventoryContext = "Inventaris saat ini kosong.") }
                    }
                }
            }
        }
    }

    fun updateInput(text: String) {
        _uiState.update { it.copy(inputText = text, error = null) }
    }

    fun sendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isBlank()) return
        sendMessageInternal(text)
    }

    fun sendQuickMessage(prompt: String) {
        sendMessageInternal(prompt)
    }

    private fun sendMessageInternal(text: String) {
        val userMessage = ChatMessage(role = MessageRole.USER, content = text)
        _uiState.update { state ->
            state.copy(
                messages = state.messages + userMessage,
                inputText = "",
                isLoading = true,
                error = null
            )
        }

        viewModelScope.launch {
            val currentState = _uiState.value
            val enrichedPrompt = if (currentState.inventoryContext.isNotBlank()) {
                "${currentState.inventoryContext}\n\nPertanyaan dari pengguna:\n$text"
            } else {
                text
            }

            val result = aiRepository.chat(enrichedPrompt, SystemPrompts.GENERAL_ASSISTANT)

            result.fold(
                onSuccess = { responseText ->
                    val aiMessage = ChatMessage(role = MessageRole.ASSISTANT, content = responseText)
                    _uiState.update { state ->
                        state.copy(messages = state.messages + aiMessage, isLoading = false)
                    }
                },
                onFailure = { error ->
                    val errorMessage = ChatMessage(
                        role = MessageRole.ASSISTANT,
                        content = "Maaf, terjadi kesalahan: ${error.message ?: "Unknown error"}. Pastikan API key sudah dikonfigurasi."
                    )
                    _uiState.update { state ->
                        state.copy(messages = state.messages + errorMessage, isLoading = false)
                    }
                }
            )
        }
    }

    fun clearConversation() {
        _uiState.update { current ->
            AIAssistantUiState(inventoryContext = current.inventoryContext)
        }
    }
}
