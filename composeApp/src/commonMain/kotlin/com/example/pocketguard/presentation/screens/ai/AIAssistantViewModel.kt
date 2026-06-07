package com.example.pocketguard.presentation.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketguard.data.remote.api.SystemPrompts
import com.example.pocketguard.domain.repository.AIRepository
import com.example.pocketguard.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class ChatMessage(
    val text: String,
    val isUser: Boolean
)

data class AIAssistantUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

private fun formatTimestamp(timestamp: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${dateTime.dayOfMonth}/${dateTime.monthNumber}/${dateTime.year}"
}

class AIAssistantViewModel(
    private val aiRepository: AIRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AIAssistantUiState())
    val uiState: StateFlow<AIAssistantUiState> = _uiState.asStateFlow()

    fun sendMessage(prompt: String) {
        if (prompt.isBlank()) return

        val currentMessages = _uiState.value.messages.toMutableList()
        currentMessages.add(ChatMessage(text = prompt, isUser = true))

        _uiState.update { it.copy(messages = currentMessages, isLoading = true, error = null) }

        viewModelScope.launch {
            // Mengambil data dan memproses semuanya di dalam blok coroutine yang sama
            val transactions = transactionRepository.getAllTransactions().first()
            val transactionSummary = transactions.joinToString(separator = "\n") { transaction ->
                "- ${formatTimestamp(transaction.createdAt)}: ${transaction.description} (Rp ${transaction.amount}) - Kategori: ${transaction.category.displayName}"
            }

            val userPrompt = """
                Berikut adalah riwayat transaksi keuangan saya saat ini:
                $transactionSummary
                
                Pertanyaan/Perintah saya:
                "$prompt"
            """.trimIndent()

            val result = aiRepository.chat(
                message = userPrompt,
                systemPrompt = SystemPrompts.FINANCIAL_ASSISTANT
            )

            result.onSuccess { aiResponse ->
                val updatedMessages = _uiState.value.messages.toMutableList()
                updatedMessages.add(ChatMessage(text = aiResponse, isUser = false))
                _uiState.update { it.copy(messages = updatedMessages, isLoading = false) }
            }.onFailure { err ->
                val updatedMessages = _uiState.value.messages.toMutableList()
                updatedMessages.add(ChatMessage(text = "Maaf, terjadi kesalahan: ${err.message}", isUser = false))
                _uiState.update { it.copy(messages = updatedMessages, isLoading = false, error = err.message) }
            }
        }
    }
}