package com.example.neurodeck.presentation.screens.aichat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neurodeck.domain.model.ChatMessage
import com.example.neurodeck.domain.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * UI state untuk AI Chat tab.
 *
 * @property messages          Chat history dari DB (reactive flow).
 * @property inputText         Current text di input field (controlled component).
 * @property isAITyping        True saat AI request in flight (show typing indicator).
 * @property suggestedQuestions  Daftar saran pertanyaan untuk kickstart conversation.
 *                               Muncul saat history empty atau di state idle.
 * @property errorSnackbar     One-shot error message (consume by Screen lalu clear).
 */
data class AIChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isAITyping: Boolean = false,
    val suggestedQuestions: List<String> = DEFAULT_SUGGESTIONS,
    val errorSnackbar: String? = null,
) {
    /** Boleh kirim kalau input non-blank + AI tidak sedang typing. */
    val canSend: Boolean
        get() = inputText.trim().isNotBlank() && !isAITyping

    /** True kalau chat baru / kosong. Untuk render welcome screen + suggestions prominent. */
    val isEmpty: Boolean
        get() = messages.isEmpty()

    companion object {
        /**
         * Saran pertanyaan default — di-show saat chat history kosong.
         * Quick onboarding untuk user yang belum tau mau tanya apa.
         */
        val DEFAULT_SUGGESTIONS = listOf(
            "Jelaskan konsep recursion dalam programming",
            "Apa itu spaced repetition?",
            "Bantu saya pahami turunan kalkulus",
            "Beri contoh penggunaan polymorphism di Java",
        )
    }
}

/**
 * ViewModel untuk AI Chat tab.
 *
 * Combine:
 *   - ChatRepository.observeMessages() (reactive)
 *   - Internal state (inputText, isTyping, snackbar)
 */
class AIChatViewModel(
    private val chatRepository: ChatRepository,
) : ViewModel() {

    private val _inputText = MutableStateFlow("")
    private val _isAITyping = MutableStateFlow(false)
    private val _errorSnackbar = MutableStateFlow<String?>(null)

    val uiState: StateFlow<AIChatUiState> = combine(
        chatRepository.observeMessages(),
        _inputText,
        _isAITyping,
        _errorSnackbar,
    ) { messages, input, typing, error ->
        AIChatUiState(
            messages = messages,
            inputText = input,
            isAITyping = typing,
            suggestedQuestions = AIChatUiState.DEFAULT_SUGGESTIONS,
            errorSnackbar = error,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AIChatUiState(),
    )

    fun onInputChange(value: String) {
        _inputText.value = value.take(MAX_INPUT_LENGTH)
    }

    /**
     * Kirim pertanyaan dari user. Steps:
     *   1. Capture input → clear input field (optimistic UX)
     *   2. Set isAITyping = true
     *   3. Call repository.sendMessage() (persist + AI call + persist reply)
     *   4. Handle result: clear typing indicator, snackbar kalau error
     */
    fun send() {
        val current = uiState.value
        if (!current.canSend) return

        val message = current.inputText.trim()

        // Step 1: clear input optimistically (user lihat input bersih segera)
        _inputText.value = ""

        viewModelScope.launch {
            _isAITyping.value = true
            try {
                val result = chatRepository.sendMessage(message)
                if (result.isFailure) {
                    _errorSnackbar.value = result.exceptionOrNull()?.message
                        ?: "Gagal kirim pesan"
                }
                // Else: success — reply sudah ter-save ke DB, flow akan emit
                // updated messages list, UI auto-update.
            } finally {
                _isAITyping.value = false
            }
        }
    }

    /**
     * Send suggested question — shortcut yang langsung set input + trigger send.
     */
    fun sendSuggestion(question: String) {
        _inputText.value = question
        send()
    }

    /**
     * Clear all chat history. Dipanggil dari TopBar action.
     * NOTE: tidak ada confirm dialog di VM — caller (Screen) yang handle confirm.
     */
    fun clearHistory() {
        viewModelScope.launch {
            try {
                chatRepository.clearHistory()
            } catch (e: Exception) {
                _errorSnackbar.value = "Gagal hapus riwayat: ${e.message ?: "unknown"}"
            }
        }
    }

    fun consumeSnackbar() {
        _errorSnackbar.value = null
    }

    private companion object {
        /** Limit input supaya tidak overflow Gemini context window dan UI tidak laggy. */
        const val MAX_INPUT_LENGTH = 2_000
    }
}