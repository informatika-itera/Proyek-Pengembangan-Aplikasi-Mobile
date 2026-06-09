package com.example.foodsaver.presentation.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodsaver.domain.model.FoodItem
import com.example.foodsaver.domain.repository.AIRepository
import com.example.foodsaver.domain.repository.WritingStyle
import com.example.foodsaver.domain.usecase.GetAllFoodUseCase
import com.example.foodsaver.domain.usecase.GenerateIdeasUseCase
import com.example.foodsaver.domain.usecase.ImproveWritingUseCase
import com.example.foodsaver.domain.usecase.SummarizeNoteUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AIAssistantViewModel(
    private val aiRepository: AIRepository,
    private val getAllFoodUseCase: GetAllFoodUseCase,
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
        
        if (action == AIAction.SUGGEST_FROM_INVENTORY) {
            _uiState.update { it.copy(inputText = "Bantu saya carikan ide resep dari bahan makanan yang ada di kulkas.") }
        }
    }
    
    fun executeAction() {
        val state = _uiState.value
        
        if (state.inputText.isBlank() && state.selectedAction != AIAction.SUGGEST_FROM_INVENTORY) {
            _uiState.update { it.copy(error = "Tuliskan sesuatu dulu ya sebelum bertanya ke AI.") }
            return
        }
        
        _uiState.update { it.copy(isLoading = true, error = null, result = null) }
        
        viewModelScope.launch {
            val result = when (state.selectedAction) {
                AIAction.SUGGEST_FROM_INVENTORY -> suggestFromInventory()
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
                    _uiState.update { it.copy(isLoading = false, error = error.message ?: "Waduh, ada kendala teknis nih. Coba lagi yuk!") }
                }
        }
    }

    private suspend fun suggestFromInventory(): Result<String> {
        return try {
            val items = getAllFoodUseCase().first()
            if (items.isEmpty()) {
                return Result.failure(Exception("Inventarismu masih kosong nih. Tambahkan bahan makanan dulu yuk!"))
            }
            
            val nearExpiry = items.filter { it.getDaysRemaining() <= 3 && it.getDaysRemaining() >= 0 }
            val inventoryText = if (nearExpiry.isNotEmpty()) {
                "Saya punya bahan makanan yang hampir lewat batas kesegarannya: ${nearExpiry.joinToString { "${it.name} (${it.getStatusLabel()})" }}. " +
                "Serta bahan lainnya: ${items.filter { it !in nearExpiry }.joinToString { it.name }}. "
            } else {
                "Saya punya bahan makanan: ${items.joinToString { it.name }}. "
            }
            
            val prompt = inventoryText + "Berikan 3 ide resep kreatif yang bisa saya masak agar bahan tersebut tidak terbuang. Jelaskan langkah singkatnya dengan bahasa yang ramah."
            aiRepository.chat(prompt)
        } catch (e: Exception) {
            Result.failure(e)
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
    SUGGEST_FROM_INVENTORY("Cek Kulkas", "Cari ide resep dari bahan yang segera habis"),
    SUMMARIZE("Ringkas", "Buat ringkasan singkat dari teks"),
    GENERATE_IDEAS("Ide Kreatif", "Dapatkan ide-ide baru untuk topikmu"),
    IMPROVE_WRITING("Perbaiki Teks", "Buat tulisanmu jadi lebih rapi"),
    TRANSLATE("Terjemahkan", "Ganti teks ke bahasa pilihanmu"),
    SUGGEST_TITLE("Saran Judul", "Dapatkan judul menarik untuk catatanmu"),
    CHAT("Tanya Bebas", "Ngobrol santai atau tanya apapun ke AI")
}

data class AIAssistantUiState(
    val inputText: String = "",
    val selectedAction: AIAction = AIAction.SUGGEST_FROM_INVENTORY,
    val writingStyle: WritingStyle = WritingStyle.NEUTRAL,
    val targetLanguage: String = "Indonesia",
    val isLoading: Boolean = false,
    val result: String? = null,
    val error: String? = null
) {
    val canExecute: Boolean
        get() = (inputText.isNotBlank() || selectedAction == AIAction.SUGGEST_FROM_INVENTORY) && !isLoading
}

sealed interface AIAssistantEvent {
    data class CopyToClipboard(val text: String) : AIAssistantEvent
    data class ApplyToNote(val text: String) : AIAssistantEvent
}
