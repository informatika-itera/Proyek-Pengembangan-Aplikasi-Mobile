package com.example.gamenews.presentation.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamenews.domain.repository.AIRepository
import com.example.gamenews.domain.repository.WritingStyle
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

    fun onInputTextChange(text: String) {
        _uiState.update { it.copy(inputText = text, error = null) }
    }

    fun onActionSelected(action: AIAction) {
        _uiState.update { it.copy(selectedAction = action) }
    }

    fun executeAction() {
        val state = _uiState.value

        if (state.inputText.isBlank()) {
            _uiState.update { it.copy(error = "Masukkan teks terlebih dahulu") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null, result = null) }

        viewModelScope.launch {
            // Langsung panggil repository AI tanpa UseCase NoteAI yang sudah dihapus
            val result = when (state.selectedAction) {
                AIAction.SUMMARIZE -> aiRepository.chat("Berikan ringkasan berita game berikut: ${state.inputText}")
                AIAction.GENERATE_IDEAS -> aiRepository.chat("Berikan ide konten/artikel tentang game: ${state.inputText}")
                AIAction.IMPROVE_WRITING -> aiRepository.chat("Perbaiki gaya penulisan berita game ini menjadi lebih menarik: ${state.inputText}")
                AIAction.TRANSLATE -> aiRepository.translate(state.inputText, state.targetLanguage)
                AIAction.SUGGEST_TITLE -> aiRepository.suggestTitle(state.inputText)
                AIAction.CHAT -> aiRepository.chat(state.inputText)
            }

            result
                .onSuccess { output ->
                    _uiState.update { it.copy(isLoading = false, result = output) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message ?: "Terjadi kesalahan") }
                }
        }
    }

    fun copyResult() {
        viewModelScope.launch {
            _events.emit(AIAssistantEvent.CopyToClipboard) // Tanpa parameter
        }
    }
}

enum class AIAction(val displayName: String, val description: String) {
    SUMMARIZE("Ringkas Berita", "Buat ringkasan berita game"),
    GENERATE_IDEAS("Ide Konten", "Generate ide artikel game"),
    IMPROVE_WRITING("Perbaiki Tulisan", "Perbaiki gaya bahasa berita"),
    TRANSLATE("Terjemah", "Terjemahkan informasi game"),
    SUGGEST_TITLE("Saran Judul", "Berikan judul berita yang menarik"),
    CHAT("Tanya AI", "Tanya apapun tentang info game")
}

data class AIAssistantUiState(
    val inputText: String = "",
    val selectedAction: AIAction = AIAction.CHAT,
    val writingStyle: WritingStyle = WritingStyle.NEUTRAL,
    val targetLanguage: String = "Indonesia",
    val isLoading: Boolean = false,
    val result: String? = null,
    val error: String? = null
) {
    val canExecute: Boolean
        get() = inputText.isNotBlank() && !isLoading
}

sealed interface AIAssistantEvent {
    data object CopyToClipboard : AIAssistantEvent // Ubah jadi data object
}