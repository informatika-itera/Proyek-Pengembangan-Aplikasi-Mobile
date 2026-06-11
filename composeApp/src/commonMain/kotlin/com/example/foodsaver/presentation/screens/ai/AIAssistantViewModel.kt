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
            _uiState.update { it.copy(error = "API key Gemini belum dikonfigurasi. Tambahkan GEMINI_API_KEY di local.properties.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null, result = null) }
        
        viewModelScope.launch {
            try {
                // Perbaikan 1: Ambil hanya data inventory AKTIF (sama dengan Home Screen)
                val allItems = getAllFoodUseCase().first()
                val activeItems = allItems.filter { !it.isConsumed && !it.isDiscarded }
                
                // Perbaikan 2: Hitung statistik di aplikasi, bukan oleh AI
                val totalActive = activeItems.size
                val safeCount = activeItems.count { it.getStatus() == FoodStatus.SAFE }
                val nearlyExpiredCount = activeItems.count { it.getStatus() == FoodStatus.NEAR_EXPIRY }
                val expiredCount = activeItems.count { it.getStatus() == FoodStatus.EXPIRED || it.getStatus() == FoodStatus.EXPIRED_TODAY }

                // Debug Log (Perbaikan 6)
                println("AI inventory count: $totalActive")

                val prompt = buildFoodSaverPrompt(
                    mode = state.selectedAction,
                    userInput = state.inputText,
                    inventoryItems = activeItems,
                    totalActive = totalActive,
                    safeCount = safeCount,
                    nearlyExpiredCount = nearlyExpiredCount,
                    expiredCount = expiredCount
                )

                // Debug Log Prompt (Perbaikan 6)
                // println("AI prompt: $prompt")

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
                        val errorMessage = error.message ?: "Terjadi kesalahan teknis."
                        val fallback = getFallbackResponse(state.selectedAction, state.inputText, activeItems)
                        
                        if (fallback != null) {
                            _uiState.update { it.copy(
                                isLoading = false, 
                                result = sanitizeAiResponse(fallback), 
                                error = "Gagal terhubung ke AI: $errorMessage. Menampilkan saran alternatif."
                            ) }
                        } else {
                            _uiState.update { it.copy(isLoading = false, error = errorMessage) }
                        }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Gagal memproses data inventory.") }
            }
        }
    }

    // Perbaikan 3: Prompt eksplisit dan akurat
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
            Data inventory pengguna:
            Total makanan aktif: $totalActive
            Masih aman: $safeCount
            Perlu segera dimasak: $nearlyExpiredCount
            Lewat tanggal: $expiredCount
            
            Daftar makanan aktif:
            ${inventoryItems.joinToString("\n") { item ->
                "- ${item.name}: ${item.quantity} ${item.unit}, Lokasi: ${item.storageLocation}, Status: ${item.getStatusLabel()}, Sisa hari: ${item.getDaysRemaining()}"
            }}
            """.trimIndent()
        } else {
            "Total makanan aktif: 0\nDaftar makanan aktif: kosong"
        }

        val instruction = if (totalActive == 0) {
            "Inventory masih kosong. Jelaskan bahwa belum ada makanan yang tercatat. Jangan mengarang data. Jangan menyebut ada stok makanan jika total aktif 0."
        } else {
            "Jawab HANYA berdasarkan data inventory di atas. JANGAN mengarang jumlah makanan. JANGAN menambahkan bahan lain. Total makanan aktif saat ini ADALAH $totalActive, jadi jangan menyebut angka selain $totalActive untuk total stok."
        }

        return """
            Mode: ${mode.displayName}
            
            $inventoryContext
            
            Pertanyaan pengguna:
            $userInput
            
            Instruksi:
            $instruction
            Gunakan Bahasa Indonesia.
            JANGAN gunakan format Markdown (# atau *).
            
            Jika mode Ringkas Stok, gunakan format ini:
            Ringkasan Stok:
            - Total stok: $totalActive
            - Masih aman: $safeCount
            - Perlu segera dimasak: $nearlyExpiredCount
            - Lewat tanggal: $expiredCount

            Prioritas Hari Ini:
            - (sebutkan bahan yang paling urgent)

            Saran FoodSaver:
            - (saran praktis)
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
                    "Inventory kamu masih kosong. Tambahkan makanan terlebih dahulu agar FoodSaver bisa membantu memantau stok."
                } else {
                    val total = inventory.size
                    val expiring = inventory.count { it.getStatus() == FoodStatus.NEAR_EXPIRY }
                    "Ringkasan Stok:\n- Total stok: $total\n- Perlu segera dimasak: $expiring\nSemua data sinkron dengan inventory aplikasi."
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
    COOKING_IDEAS("Ide Masak", "Ide kreatif masakan seadanya", "Contoh: Beri ide masakan praktis"),
    
    CHAT("Tanya Bebas", "Ngobrol santai dengan AI", "Tanya apa saja..."),
    SUMMARIZE("Ringkas Teks", "Ringkas teks apa saja", ""),
    GENERATE_IDEAS("Ide Kreatif", "Dapatkan ide untuk topikmu", ""),
    IMPROVE_WRITING("Perbaiki Teks", "Buat tulisanmu jadi rapi", ""),
    TRANSLATE("Terjemahkan", "Ganti teks ke bahasa lain", ""),
    SUGGEST_TITLE("Saran Judul", "Dapatkan judul menarik", "")
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
