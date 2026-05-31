package com.kelazzz.app.presentation.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelazzz.app.domain.model.ChatMessage
import com.kelazzz.app.domain.repository.AIRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI State untuk halaman AI Chatbot
 */
data class AIChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel untuk AI Chatbot Asisten Akademik
 *
 * Mengelola:
 * - Daftar pesan chat (user + assistant)
 * - Loading state saat menunggu respons AI
 * - Error handling
 * - Quick suggestion list
 */
class AIViewModel(
    private val aiRepository: AIRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AIChatUiState())
    val uiState: StateFlow<AIChatUiState> = _uiState.asStateFlow()

    /**
     * Quick suggestions yang ditampilkan saat chat kosong
     */
    val quickSuggestions = listOf(
        "Jadwal kuliah saya hari ini apa?",
        "Rekap kehadiran saya",
        "Mata kuliah alpha terbanyak?",
        "Tips belajar efektif"
    )

    /**
     * Kirim pesan ke AI chatbot
     *
     * Flow:
     * 1. Tambah pesan user ke list
     * 2. Tambah loading message (typing indicator)
     * 3. Panggil AIRepository.chat() dengan history
     * 4. Ganti loading message dengan respons AI
     */
    fun sendMessage(text: String) {
        val trimmedText = text.trim()
        if (trimmedText.isBlank()) return

        val userMessage = ChatMessage.userMessage(trimmedText)
        val loadingMessage = ChatMessage.loadingMessage()

        // Tambah pesan user + loading indicator
        _uiState.update { state ->
            state.copy(
                messages = state.messages + userMessage + loadingMessage,
                isLoading = true,
                error = null
            )
        }

        viewModelScope.launch {
            // Ambil history (tanpa loading message) untuk context multi-turn
            val history = _uiState.value.messages.filter { !it.isLoading }

            val result = aiRepository.chat(
                message = trimmedText,
                history = history.dropLast(1) // Exclude pesan user yang baru dikirim (sudah di prompt)
            )

            result.fold(
                onSuccess = { response ->
                    val assistantMessage = ChatMessage.assistantMessage(response)
                    _uiState.update { state ->
                        state.copy(
                            messages = state.messages
                                .filter { !it.isLoading } + assistantMessage,
                            isLoading = false
                        )
                    }
                },
                onFailure = { error ->
                    val errorMessage = ChatMessage.assistantMessage(
                        "Maaf, terjadi kesalahan: ${error.message ?: "Tidak dapat terhubung ke AI"}. Silakan coba lagi."
                    )
                    _uiState.update { state ->
                        state.copy(
                            messages = state.messages
                                .filter { !it.isLoading } + errorMessage,
                            isLoading = false,
                            error = error.message
                        )
                    }
                }
            )
        }
    }

    /**
     * Bersihkan semua pesan chat
     */
    fun clearChat() {
        aiRepository.clearHistory()
        _uiState.update {
            AIChatUiState()
        }
    }

    /**
     * Bersihkan error
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
