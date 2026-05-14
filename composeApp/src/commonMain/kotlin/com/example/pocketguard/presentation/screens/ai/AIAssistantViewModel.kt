package com.example.pocketguard.presentation.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketguard.domain.repository.AIRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AIAssistantViewModel(
    private val aiRepository: AIRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AIAssistantUiState())
    val uiState: StateFlow<AIAssistantUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AIAssistantEvent>()
    val events: SharedFlow<AIAssistantEvent> = _events.asSharedFlow()

    fun setInitialText(text: String?) {
        if (text != null && _uiState.value.inputText.isBlank()) {
            _uiState.update { it.copy(inputText = text) }
        }
    }

    fun onInputTextChange(text: String) {
        _uiState.update { it.copy(inputText = text, error = null) }
    }

    fun onActionSelected(action: AIAction) {
        _uiState.update { it.copy(selectedAction = action) }
    }

    fun executeAction() {
        val state = _uiState.value
        if (state.inputText.isBlank()) {
            _uiState.update { it.copy(error = "Masukkan konteks data atau pertanyaan") }
            return
        }

        _uiState.update { it.copy(isLoading = true, result = null, error = null) }

        viewModelScope.launch {
            // Membangun prompt berdasarkan aksi yang dipilih
            val prompt = buildPrompt(state.selectedAction, state.inputText)

            val result = aiRepository.summarize(prompt)

            result.onSuccess { text ->
                _uiState.update { it.copy(result = text, isLoading = false) }
            }.onFailure { err ->
                _uiState.update { it.copy(isLoading = false, error = err.message) }
                _events.emit(AIAssistantEvent.Error(err.message ?: "Gagal terhubung ke AI"))
            }
        }
    }

    private fun buildPrompt(action: AIAction, context: String): String {
        return when (action) {
            AIAction.ANALYZE -> """
                Sebagai asisten keuangan PocketGuard, analisis data transaksi berikut. 
                Berikan ringkasan pengeluaran dan kategori apa yang paling boros:
                $context
            """.trimIndent()

            AIAction.SAVING_TIPS -> """
                Berdasarkan data keuangan berikut, berikan 3 tips spesifik untuk menghemat uang 
                dan memperbaiki kebiasaan belanja user:
                $context
            """.trimIndent()

            AIAction.PLANNING -> """
                Bantu user membuat rencana anggaran (budgeting) untuk bulan depan 
                berdasarkan pola transaksi ini:
                $context
            """.trimIndent()
        }
    }

    fun copyResult() {
        _uiState.value.result?.let {
            viewModelScope.launch {
                _events.emit(AIAssistantEvent.CopyToClipboard(it))
            }
        }
    }

    fun applyToTransaction() {
        _uiState.value.result?.let {
            viewModelScope.launch {
                _events.emit(AIAssistantEvent.ApplyToTransaction(it))
            }
        }
    }
}

// ==================== STATE & MODELS ====================

data class AIAssistantUiState(
    val inputText: String = "",
    val selectedAction: AIAction = AIAction.ANALYZE,
    val isLoading: Boolean = false,
    val result: String? = null,
    val error: String? = null
) {
    val canExecute: Boolean get() = inputText.isNotBlank() && !isLoading
}

enum class AIAction(val displayName: String, val description: String) {
    ANALYZE("Analisis", "Menganalisis pola transaksi kamu."),
    SAVING_TIPS("Tips Hemat", "Saran cara menghemat pengeluaran."),
    PLANNING("Budgeting", "Membantu merencanakan anggaran.")
}

sealed interface AIAssistantEvent {
    data class CopyToClipboard(val text: String) : AIAssistantEvent
    data class ApplyToTransaction(val text: String) : AIAssistantEvent
    data class Error(val message: String) : AIAssistantEvent
}