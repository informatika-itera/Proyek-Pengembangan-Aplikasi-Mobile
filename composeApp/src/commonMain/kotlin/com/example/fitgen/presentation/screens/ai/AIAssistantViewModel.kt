package com.example.fitgen.presentation.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitgen.domain.repository.AIRepository
import com.example.fitgen.domain.repository.WritingStyle
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
        text?.let { _uiState.update { state -> state.copy(inputText = it) } }
    }
    fun onInputTextChange(text: String) { _uiState.update { it.copy(inputText = text, error = null) } }
    fun onActionSelected(action: AIAction) { _uiState.update { it.copy(selectedAction = action) } }

    fun executeAction() {
        val state = _uiState.value
        if (state.inputText.isBlank()) { _uiState.update { it.copy(error = "Masukkan teks terlebih dahulu") }; return }
        _uiState.update { it.copy(isLoading = true, error = null, result = null) }
        viewModelScope.launch {
            val result = when (state.selectedAction) {
                AIAction.SUMMARIZE       -> aiRepository.summarize(state.inputText)
                AIAction.GENERATE_IDEAS  -> aiRepository.generateIdeas(state.inputText).map { list -> list.mapIndexed { i, s -> "${i+1}. $s" }.joinToString("\n") }
                AIAction.IMPROVE_WRITING -> aiRepository.improveWriting(state.inputText, state.writingStyle)
                AIAction.TRANSLATE       -> aiRepository.translate(state.inputText, state.targetLanguage)
                AIAction.SUGGEST_TITLE   -> aiRepository.suggestTitle(state.inputText)
                AIAction.CHAT            -> aiRepository.chat(state.inputText)
            }
            result.onSuccess { output -> _uiState.update { it.copy(isLoading = false, result = output) } }
                  .onFailure { error  -> _uiState.update { it.copy(isLoading = false, error = error.message ?: "Terjadi kesalahan") } }
        }
    }

    fun copyResult() { val r = _uiState.value.result; if (r != null) viewModelScope.launch { _events.emit(AIAssistantEvent.CopyToClipboard(r)) } }
    fun applyResult() { val r = _uiState.value.result; if (r != null) viewModelScope.launch { _events.emit(AIAssistantEvent.ApplyResult(r)) } }
    fun onWritingStyleChange(style: WritingStyle) { _uiState.update { it.copy(writingStyle = style) } }
    fun onTargetLanguageChange(language: String)  { _uiState.update { it.copy(targetLanguage = language) } }
}

enum class AIAction(val displayName: String, val description: String) {
    SUMMARIZE("Ringkas", "Buat ringkasan dari teks"),
    GENERATE_IDEAS("Ide", "Generate ide berdasarkan topik"),
    IMPROVE_WRITING("Perbaiki", "Perbaiki tulisan"),
    TRANSLATE("Terjemah", "Terjemahkan ke bahasa lain"),
    SUGGEST_TITLE("Judul", "Sarankan judul"),
    CHAT("Tanya", "Tanya AI tentang apapun")
}

data class AIAssistantUiState(
    val inputText: String = "",
    val selectedAction: AIAction = AIAction.SUMMARIZE,
    val writingStyle: WritingStyle = WritingStyle.NEUTRAL,
    val targetLanguage: String = "English",
    val isLoading: Boolean = false,
    val result: String? = null,
    val error: String? = null
) {
    val canExecute: Boolean get() = inputText.isNotBlank() && !isLoading
}

sealed interface AIAssistantEvent {
    data class CopyToClipboard(val text: String) : AIAssistantEvent
    data class ApplyResult(val text: String) : AIAssistantEvent
}
