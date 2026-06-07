package com.example.synesthesia.presentation.screens.sanctuary

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.synesthesia.domain.repository.AIRepository
import com.example.synesthesia.domain.model.EmotionSystem
import com.example.synesthesia.domain.repository.NoteRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus

class SanctuaryViewModel(
    private val repository: NoteRepository,
    private val aiRepository: AIRepository
) : ViewModel() {
    
    private val tz = TimeZone.currentSystemDefault()

    private val _aiRecommendation = MutableStateFlow<MoodRecommendation?>(null)
    val aiRecommendation = _aiRecommendation.asStateFlow()

    private val _isGeneratingRecommendation = MutableStateFlow(false)
    val isGeneratingRecommendation = _isGeneratingRecommendation.asStateFlow()

    // --- Grounding 5-4-3-2-1 ---
    private val _groundingStep = MutableStateFlow(0) // 0: inactive, 1: See, 2: Touch, etc.
    val groundingStep = _groundingStep.asStateFlow()
    
    private val _groundingList = MutableStateFlow<List<String>>(emptyList())
    val groundingList = _groundingList.asStateFlow()

    // --- Worry Vault ---
    private val _worryVaultText = MutableStateFlow("")
    val worryVaultText = _worryVaultText.asStateFlow()
    
    private val _isVaultLocked = MutableStateFlow(false)
    val isVaultLocked = _isVaultLocked.asStateFlow()
    
    private val _vaultAiResponse = MutableStateFlow<String?>(null)
    val vaultAiResponse = _vaultAiResponse.asStateFlow()

    init {
        generateAiRecommendation()
    }

    fun generateAiRecommendation() {
        viewModelScope.launch {
            _isGeneratingRecommendation.value = true
            repository.getAllNotes().first().let { notes ->
                val recent = notes.take(3)
                if (recent.isEmpty()) {
                    _aiRecommendation.value = MoodRecommendation(
                        title = "Mulai Menulis",
                        message = "Tulis jurnal pertamamu untuk mendapatkan rekomendasi personal dari AI.",
                        suggestedRituals = listOf("breathing"),
                        accentColor = Color(0xFF34D399)
                    )
                    _isGeneratingRecommendation.value = false
                    return@launch
                }

                val emotionSummary = recent.map { it.emotion }.joinToString(", ")
                val prompt = """
                    Berdasarkan 3 emosi terakhir user: $emotionSummary.
                    Rekomendasikan 1 ritual dari daftar ini: Breathing, Meditation, Sleep Well, Focus Flow, Gratitude, Energy Boost.
                    Berikan alasan singkat dan empatik. Balas dalam format JSON:
                    { "ritualId": "id_dari_daftar_kecil", "reason": "alasan" }
                    IDs: breathing, meditation, sleep, focus, gratitude, energy.
                """.trimIndent()

                aiRepository.chat(prompt).onSuccess { json ->
                    val ritualId = if (json.contains("meditation")) "meditation" else if (json.contains("focus")) "focus" else if (json.contains("sleep")) "sleep" else if (json.contains("gratitude")) "gratitude" else if (json.contains("energy")) "energy" else "breathing"
                    val reason = if (json.contains("\"reason\": \"")) json.substringAfter("\"reason\": \"").substringBefore("\"") else "AI merekomendasikan sesi ini untuk keseimbanganmu."
                    
                    _aiRecommendation.value = MoodRecommendation(
                        title = "Rekomendasi AI ✨",
                        message = reason,
                        suggestedRituals = listOf(ritualId),
                        accentColor = Color(0xFF34D399) // Default, we'll fix later
                    )
                }
            }
            _isGeneratingRecommendation.value = false
        }
    }

    fun startGrounding() {
        _groundingStep.value = 1
        _groundingList.value = emptyList()
    }

    fun addGroundingInput(text: String) {
        _groundingList.update { it + text }
        if (_groundingStep.value < 5) {
            _groundingStep.update { it + 1 }
        } else {
            _groundingStep.value = 6 // Final validation state
            viewModelScope.launch {
                val inputStr = _groundingList.value.joinToString(", ")
                val prompt = "User baru saja melakukan grounding 5-4-3-2-1 dengan input: $inputStr. Berikan satu kalimat penenang yang singkat dan empatik."
                aiRepository.chat(prompt).onSuccess { res ->
                    _vaultAiResponse.value = res
                }
            }
        }
    }

    fun setWorryText(text: String) {
        _worryVaultText.value = text
    }

    fun lockWorryVault() {
        if (_worryVaultText.value.isBlank()) return
        _isVaultLocked.value = true
        viewModelScope.launch {
            val prompt = "User merasa khawatir tentang: ${_worryVaultText.value}. Berikan counter-thought positif yang logis dan singkat (max 20 kata)."
            aiRepository.chat(prompt).onSuccess { res ->
                _vaultAiResponse.value = res
            }
        }
    }
    
    fun resetVault() {
        _worryVaultText.value = ""
        _isVaultLocked.value = false
        _vaultAiResponse.value = null
    }
    
    fun resetGrounding() {
        _groundingStep.value = 0
        _groundingList.value = emptyList()
        _vaultAiResponse.value = null
    }
}

data class MoodRecommendation(
    val title: String,
    val message: String,
    val suggestedRituals: List<String>,
    val accentColor: Color
)
