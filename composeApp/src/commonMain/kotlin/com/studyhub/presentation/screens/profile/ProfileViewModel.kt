package com.studyhub.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyhub.data.remote.api.GroqService
import com.studyhub.data.remote.api.SystemPrompts
import com.studyhub.domain.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ProfileUiState(
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val aiInsight: String = "Klik tombol di bawah untuk mendapatkan insight belajar dari AI.",
    val isInsightLoading: Boolean = false,
    val error: String? = null
)

class ProfileViewModel(
    private val taskRepository: TaskRepository,
    private val groqService: GroqService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            taskRepository.getAllTasks().collect { tasks ->
                _uiState.value = _uiState.value.copy(
                    totalTasks = tasks.size,
                    completedTasks = tasks.count { it.progress == 100 }
                )
            }
        }
    }

    fun generateAiInsight() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isInsightLoading = true, error = null)
            
            val tasks = taskRepository.getAllTasks().first()
            val taskSummary = tasks.joinToString("\n") { 
                "- ${it.title} (${it.category}): ${it.progress}% selesai" 
            }
            
            val prompt = """
                Berikut adalah daftar tugas saya:
                ${if (tasks.isEmpty()) "Belum ada tugas." else taskSummary}
                
                Berikan insight singkat tentang kemajuan belajar saya.
            """.trimIndent()

            groqService.generateContent(
                prompt = prompt,
                systemPrompt = SystemPrompts.STUDY_INSIGHT
            ).onSuccess { insight ->
                _uiState.value = _uiState.value.copy(
                    aiInsight = insight,
                    isInsightLoading = false
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Gagal mendapatkan insight",
                    isInsightLoading = false
                )
            }
        }
    }
}
