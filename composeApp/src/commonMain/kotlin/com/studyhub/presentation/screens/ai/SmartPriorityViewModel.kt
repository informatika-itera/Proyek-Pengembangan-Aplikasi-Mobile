package com.studyhub.presentation.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyhub.data.local.AiUsageLimit
import com.studyhub.domain.model.AiUsageStats
import com.studyhub.domain.model.PriorityResult
import com.studyhub.domain.model.Task
import com.studyhub.domain.repository.AiError
import com.studyhub.domain.usecase.ai.GetAiUsageStatsUseCase
import com.studyhub.domain.usecase.ai.GetSmartPriorityUseCase
import com.studyhub.domain.usecase.task.GetActiveTasksUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed interface SmartPriorityUiState {
    object Idle : SmartPriorityUiState
    object Loading : SmartPriorityUiState
    data class Success(
        val prioritizedTasks: List<Pair<Task, PriorityResult>>,
        val fromCache: Boolean,
        val usageStats: AiUsageStats
    ) : SmartPriorityUiState
    data class Error(
        val message: String,
        val fallbackTasks: List<Task>
    ) : SmartPriorityUiState
    object QuotaExceeded : SmartPriorityUiState
}

class SmartPriorityViewModel(
    private val getSmartPriorityUseCase: GetSmartPriorityUseCase,
    private val getActiveTasksUseCase: GetActiveTasksUseCase,
    private val getAiUsageStatsUseCase: GetAiUsageStatsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SmartPriorityUiState>(
        SmartPriorityUiState.Idle
    )
    val uiState: StateFlow<SmartPriorityUiState> =
        _uiState.asStateFlow()

    fun loadPriority() {
        viewModelScope.launch {
            _uiState.value = SmartPriorityUiState.Loading
            try {
                val stats = getAiUsageStatsUseCase()
                if (stats.priorityCallsToday >=
                    AiUsageLimit.MAX_PRIORITY_PER_DAY) {
                    _uiState.value = SmartPriorityUiState.QuotaExceeded
                    return@launch
                }

                val priorityResults = getSmartPriorityUseCase()
                val allTasks = getActiveTasksUseCase().first()

                val paired = priorityResults
                    .sortedBy { it.priorityOrder }
                    .mapNotNull { result ->
                        val task = allTasks.find {
                            it.id == result.taskId
                        }
                        task?.let { Pair(it, result) }
                    }

                _uiState.value = SmartPriorityUiState.Success(
                    prioritizedTasks = paired,
                    fromCache = false, // Implementation of cached flag would require more repo changes, assuming false for now or inferred from repo
                    usageStats = getAiUsageStatsUseCase()
                )
            } catch (e: Exception) {
                val fallback = getActiveTasksUseCase().first()
                val message = when (e) {
                    is AiError.NoInternet -> "Tidak ada koneksi internet"
                    is AiError.QuotaExceeded -> "Batas penggunaan hari ini tercapai"
                    is AiError.ApiError -> "Layanan AI sedang tidak tersedia"
                    else -> e.message ?: "Terjadi kesalahan"
                }
                _uiState.value = SmartPriorityUiState.Error(
                    message = message,
                    fallbackTasks = fallback
                )
            }
        }
    }

    fun refresh() { loadPriority() }
}
