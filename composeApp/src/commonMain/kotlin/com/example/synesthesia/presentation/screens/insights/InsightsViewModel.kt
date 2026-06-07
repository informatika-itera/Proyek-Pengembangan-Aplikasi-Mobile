package com.example.synesthesia.presentation.screens.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.synesthesia.data.local.datastore.UserPreferences
import com.example.synesthesia.domain.repository.AIRepository
import com.example.synesthesia.domain.model.EmotionSystem
import com.example.synesthesia.domain.model.Note
import com.example.synesthesia.domain.repository.NoteRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

class InsightsViewModel(
    private val repository: NoteRepository,
    private val userPreferences: UserPreferences,
    private val aiRepository: AIRepository
) : ViewModel() {
    
    private val tz = TimeZone.currentSystemDefault()
    private val now = Clock.System.now()

    private val _isEditingProfile = MutableStateFlow(false)
    val isEditingProfile = _isEditingProfile.asStateFlow()

    private val _weeklySummary = MutableStateFlow<String?>(null)
    val weeklySummary = _weeklySummary.asStateFlow()

    private val _isGeneratingSummary = MutableStateFlow(false)
    val isGeneratingSummary = _isGeneratingSummary.asStateFlow()

    val userName = userPreferences.userName.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Stargazer")
    val userBio = userPreferences.userBio.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    val userPhotoUri = userPreferences.userPhotoUri.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val uiState: StateFlow<InsightsUiState> = repository.getAllNotes()
        .map { notes ->
            val total = notes.size
            if (total == 0) return@map InsightsUiState.Empty

            val distribution = EmotionSystem.categories.associate { category ->
                val count = notes.count { note ->
                    note.artToken == category.name || category.subEmotions.contains(note.emotion)
                }
                category.id to (count.toFloat() / total * 100).toInt()
            }

            val last7DaysData = (0..6).map { i ->
                val date = now.minus(i, DateTimeUnit.DAY, tz)
                val dayNotes = notes.filter { it.createdAt.toLocalDateTime(tz).date == date.toLocalDateTime(tz).date }
                val count = dayNotes.size.toFloat()
                val dominant = dayNotes.groupBy { it.emotion }.maxByOrNull { it.value.size }?.key
                val dominantCategoryId = EmotionSystem.getCategoryBySubEmotion(dominant)?.id ?: "LEP"
                count to dominantCategoryId
            }.reversed()

            val currentMonth = now.toLocalDateTime(tz).month
            val calendarData = notes
                .filter { note -> 
                    note.createdAt.toLocalDateTime(tz).month == currentMonth 
                }
                .groupBy { note -> note.createdAt.toLocalDateTime(tz).dayOfMonth }
                .mapValues { (_, dayNotes) ->
                    val dominant = dayNotes.groupBy { it.emotion }.maxByOrNull { it.value.size }?.key
                    EmotionSystem.categories.find { it.subEmotions.contains(dominant) }?.color
                }

            InsightsUiState.Success(
                totalMemories = total,
                emotionDistribution = distribution,
                weeklyTrend = last7DaysData.map { it.first },
                weeklyDominantEmotions = last7DaysData.map { it.second },
                calendarData = calendarData,
                currentMonthName = currentMonth.name,
                currentYear = now.toLocalDateTime(tz).year
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InsightsUiState.Loading)

    fun toggleEditProfile() {
        _isEditingProfile.update { !it }
    }

    fun updateProfile(name: String, bio: String, photoUri: String?) {
        viewModelScope.launch {
            userPreferences.updateProfile(name, bio, photoUri)
            _isEditingProfile.value = false
        }
    }

    fun triggerWeeklySummary() {
        viewModelScope.launch {
            repository.getAllNotes().first().let { notes ->
                generateWeeklySummary(notes)
            }
        }
    }

    private fun generateWeeklySummary(notes: List<Note>) {
        viewModelScope.launch {
            _isGeneratingSummary.value = true
            val last7Notes = notes.filter { 
                it.createdAt > Clock.System.now().minus(7, DateTimeUnit.DAY, tz) 
            }
            if (last7Notes.isEmpty()) { 
                _isGeneratingSummary.value = false
                _weeklySummary.value = "Belum ada catatan untuk dianalisis."
                return@launch 
            }
            
            val emotionSummary = last7Notes
                .groupBy { it.emotion }
                .map { "${it.key}: ${it.value.size} kali" }
                .joinToString(", ")
            
            val prompt = """
                Berikan analisis singkat (2-3 kalimat dalam Bahasa Indonesia) tentang 
                kondisi emosi seseorang berdasarkan catatan jurnal 7 hari terakhir ini:
                $emotionSummary
                Sampaikan dengan hangat dan empatik seperti seorang teman.
            """.trimIndent()
            
            aiRepository.chat(prompt).onSuccess { summary ->
                _weeklySummary.value = summary
            }.onFailure {
                _weeklySummary.value = "Gagal memproses analisis jiwa."
            }
            _isGeneratingSummary.value = false
        }
    }
}

sealed interface InsightsUiState {
    data object Loading : InsightsUiState
    data object Empty : InsightsUiState
    data class Success(
        val totalMemories: Int,
        val emotionDistribution: Map<String, Int>,
        val weeklyTrend: List<Float>,
        val weeklyDominantEmotions: List<String>,
        val calendarData: Map<Int, String?>,
        val currentMonthName: String,
        val currentYear: Int
    ) : InsightsUiState
}
