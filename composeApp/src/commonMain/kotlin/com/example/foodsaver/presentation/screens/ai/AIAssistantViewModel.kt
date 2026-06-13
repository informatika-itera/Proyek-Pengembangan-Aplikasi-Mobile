package com.example.foodsaver.presentation.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodsaver.core.network.ApiConfig
import com.example.foodsaver.data.remote.api.SystemPrompts
import com.example.foodsaver.domain.model.FoodItem
import com.example.foodsaver.domain.model.FoodStatus
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
        _uiState.update { it.copy(selectedAction = action, error = null, result = null) }
    }
    
    fun executeAction() {
        val state = _uiState.value
        
        if (ApiConfig.geminiApiKey.isBlank()) {
            _uiState.update { it.copy(error = "API key belum diatur. Selesaikan konfigurasi di Pengaturan.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null, result = null) }
        
        viewModelScope.launch {
            try {
                val allItems = getAllFoodUseCase().first()
                val activeItems = allItems.filter { !it.isConsumed && !it.isDiscarded }
                
                val totalActive = activeItems.size
                val safeCount = activeItems.count { it.getStatus() == FoodStatus.SAFE }
                val nearlyExpiredCount = activeItems.count { it.getStatus() == FoodStatus.NEAR_EXPIRY }
                val expiredCount = activeItems.count { it.getStatus() == FoodStatus.EXPIRED || it.getStatus() == FoodStatus.EXPIRED_TODAY }

                val prompt = buildFoodSaverPrompt(
                    mode = state.selectedAction,
                    userInput = state.inputText,
                    inventoryItems = activeItems,
                    totalActive = totalActive,
                    safeCount = safeCount,
                    nearlyExpiredCount = nearlyExpiredCount,
                    expiredCount = expiredCount
                )

                val systemPrompt = when (state.selectedAction) {
                    AIAction.CHECK_STOCK -> SystemPrompts.STOCK_CHECKER
                    AIAction.CREATE_RECIPE -> SystemPrompts.RECIPE_SUGGESTER
                    AIAction.STORAGE_TIPS -> SystemPrompts.STORAGE_ADVISOR
                    AIAction.SUMMARIZE_INVENTORY -> SystemPrompts.INVENTORY_SUMMARIZER
                    AIAction.COOKING_IDEAS -> SystemPrompts.COOKING_IDEAS
                    else -> SystemPrompts.BASE_FOODSAVER
                }

                val result = aiRepository.generateResponse(
                    prompt = prompt,
                    systemPrompt = systemPrompt,
                    temperature = if (state.selectedAction == AIAction.SUMMARIZE_INVENTORY) 0.3 else 0.7,
                    maxTokens = 1000
                )
                
                result
                    .onSuccess { output ->
                        val sanitized = sanitizeAiResponse(output)
                        _uiState.update { it.copy(isLoading = false, result = sanitized) }
                    }
                    .onFailure { error ->
                        val fallback = getFallbackResponse(state.selectedAction, state.inputText, activeItems)
                        
                        if (fallback != null) {
                            _uiState.update { it.copy(
                                isLoading = false, 
                                result = sanitizeAiResponse(fallback), 
                                error = "Koneksi ke AI bermasalah. Menampilkan saran alternatif."
                            ) }
                        } else {
                            _uiState.update { it.copy(isLoading = false, error = "Maaf, AI sedang tidak bisa diakses. Coba lagi nanti.") }
                        }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Terjadi kesalahan saat memproses data.") }
            }
        }
    }

    private fun buildFoodSaverPrompt(
        mode: AIAction,
        userInput: String,
        inventoryItems: List<FoodItem>,
        totalActive: Int,
        safeCount: Int,
        nearlyExpiredCount: Int,
        expiredCount: Int
    ): String {
        val inventoryContext = if (totalActive > 0) {
            """
            Data inventory:
            Total stok: $totalActive
            Aman: $safeCount
            Segera: $nearlyExpiredCount
            Lewat: $expiredCount
            
            Daftar:
            ${inventoryItems.joinToString("\n") { item ->
                "- ${item.name}: ${item.quantity} ${item.unit}, Lokasi: ${item.storageLocation}, Status: ${item.getStatusLabel()}"
            }}
            """.trimIndent()
        } else {
            "Inventory masih kosong."
        }

        return """
            Mode: ${mode.displayName}
            $inventoryContext
            Pertanyaan: $userInput
            
            Berikan jawaban yang ramah dalam Bahasa Indonesia tanpa Markdown.
        """.trimIndent()
    }
    
    private fun sanitizeAiResponse(text: String): String {
        return text
            .replace(Regex("^#{1,6}\\s*", RegexOption.MULTILINE), "")
            .replace("**", "")
            .replace("```", "")
            .trim()
    }
    
    private fun getFallbackResponse(action: AIAction, input: String, inventory: List<FoodItem>): String? {
        return when (action) {
            AIAction.SUMMARIZE_INVENTORY -> {
                if (inventory.isEmpty()) {
                    "Stok kamu masih kosong. Tambahkan makanan agar saya bisa membantu memantau."
                } else {
                    val total = inventory.size
                    val expiring = inventory.count { it.getStatus() == FoodStatus.NEAR_EXPIRY }
                    "Ringkasan Stok:\n- Total stok: $total\n- Perlu segera dimasak: $expiring\nSemua data sesuai dengan daftar makanan kamu."
                }
            }
            else -> null
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

    fun onQuickPromptClicked(prompt: String, action: AIAction) {
        _uiState.update { it.copy(selectedAction = action, inputText = prompt) }
        executeAction()
    }
}

enum class AIAction(val displayName: String, val description: String, val placeholder: String) {
    CHECK_STOCK("Cek Stok", "Bahan mana yang harus segera dipakai?", "Contoh: Cek bahan yang harus segera dipakai"),
    CREATE_RECIPE("Buat Resep", "Rekomendasi resep dari bahan tersedia", "Contoh: Saya punya bakso dan telur, masak apa?"),
    STORAGE_TIPS("Tips Simpan", "Saran agar makanan tahan lebih lama", "Contoh: Cara simpan daging biar awet?"),
    SUMMARIZE_INVENTORY("Ringkas Stok", "Kondisi keseluruhan inventorymu", "Contoh: Ringkas kondisi stok saya"),
    COOKING_IDEAS("Ide Masak", "Ide kreatif masakan seadanya", "Contoh: Beri ide masakan praktis")
}

data class AIAssistantUiState(
    val inputText: String = "",
    val selectedAction: AIAction = AIAction.CHECK_STOCK,
    val writingStyle: WritingStyle = WritingStyle.NEUTRAL,
    val targetLanguage: String = "Indonesia",
    val isLoading: Boolean = false,
    val result: String? = null,
    val error: String? = null
) {
    val canExecute: Boolean
        get() = !isLoading
}

sealed interface AIAssistantEvent {
    data class CopyToClipboard(val text: String) : AIAssistantEvent
    data class ApplyToNote(val text: String) : AIAssistantEvent
}
