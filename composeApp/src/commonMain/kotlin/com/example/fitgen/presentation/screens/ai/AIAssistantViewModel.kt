package com.example.fitgen.presentation.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitgen.domain.repository.AIRepository
import com.example.fitgen.domain.repository.WritingStyle
import com.example.fitgen.domain.usecase.GenerateIdeasUseCase
import com.example.fitgen.domain.usecase.ImproveWritingUseCase
import com.example.fitgen.domain.usecase.SummarizeNoteUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AIAssistantViewModel(
    private val aiRepository: AIRepository,
    private val summarizeUseCase: SummarizeNoteUseCase,
    private val improveWritingUseCase: ImproveWritingUseCase,
    private val generateIdeasUseCase: GenerateIdeasUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AIAssistantUiState())
    val uiState: StateFlow<AIAssistantUiState> = _uiState.asStateFlow()
    
    private val _events = MutableSharedFlow<AIAssistantEvent>()
    val events: SharedFlow<AIAssistantEvent> = _events.asSharedFlow()
    
    fun setInitialText(text: String?) {
        text?.let {
            _uiState.update { state -> state.copy(inputText = it) }
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
            _uiState.update { it.copy(error = "Masukkan teks terlebih dahulu") }
            return
        }
        
        _uiState.update { it.copy(isLoading = true, error = null, result = null) }
        
        viewModelScope.launch {
            val result = when (state.selectedAction) {
                AIAction.SUMMARIZE -> summarize(state.inputText)
                AIAction.GENERATE_IDEAS -> generateIdeas(state.inputText)
                AIAction.IMPROVE_WRITING -> improveWriting(state.inputText, state.writingStyle)
                AIAction.TRANSLATE -> translate(state.inputText, state.targetLanguage)
                AIAction.SUGGEST_TITLE -> suggestTitle(state.inputText)
                AIAction.CHAT -> chat(state.inputText)
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
        val result = _uiState.value.result
        if (result != null) {
            viewModelScope.launch {
                _events.emit(AIAssistantEvent.CopyToClipboard(result))
            }
        }
    }
    
    fun applyToNote() {
        val result = _uiState.value.result
        if (result != null) {
            viewModelScope.launch {
                _events.emit(AIAssistantEvent.ApplyToNote(result))
            }
        }
    }
    
    fun onWritingStyleChange(style: WritingStyle) {
        _uiState.update { it.copy(writingStyle = style) }
    }
    
    fun onTargetLanguageChange(language: String) {
        _uiState.update { it.copy(targetLanguage = language) }
    }
    
    // ==================== AI OPERATIONS ====================
    
    private suspend fun summarize(text: String): Result<String> {
        return summarizeUseCase(text)
    }
    
    private suspend fun generateIdeas(topic: String): Result<String> {
        return generateIdeasUseCase(topic).map { ideas ->
            ideas.mapIndexed { index, idea -> "${index + 1}. $idea" }.joinToString("\n")
        }
    }
    
    private suspend fun improveWriting(text: String, style: WritingStyle): Result<String> {
        return improveWritingUseCase(text, style)
    }
    
    private suspend fun translate(text: String, targetLanguage: String): Result<String> {
        return aiRepository.translate(text, targetLanguage)
    }
    
    private suspend fun suggestTitle(content: String): Result<String> {
        return aiRepository.suggestTitle(content)
    }
    
    private suspend fun chat(message: String): Result<String> {
        return aiRepository.chat(message)
    }
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
    val canExecute: Boolean
        get() = inputText.isNotBlank() && !isLoading
}

sealed interface AIAssistantEvent {
    data class CopyToClipboard(val text: String) : AIAssistantEvent
    data class ApplyToNote(val text: String) : AIAssistantEvent
}
