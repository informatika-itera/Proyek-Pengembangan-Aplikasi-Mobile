package com.studyhub.presentation.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyhub.data.local.AiUsageLimit
import com.studyhub.domain.model.ReminderSchedule
import com.studyhub.domain.usecase.ai.GetAiUsageStatsUseCase
import com.studyhub.domain.usecase.ai.GetSmartReminderUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface SmartReminderUiState {
    object Idle : SmartReminderUiState
    object Loading : SmartReminderUiState
    data class Success(val schedule: ReminderSchedule) : SmartReminderUiState
    data class Error(val message: String) : SmartReminderUiState
    object QuotaExceeded : SmartReminderUiState
}

class SmartReminderViewModel(
    private val getSmartReminderUseCase: GetSmartReminderUseCase,
    private val getAiUsageStatsUseCase: GetAiUsageStatsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SmartReminderUiState>(SmartReminderUiState.Idle)
    val uiState: StateFlow<SmartReminderUiState> = _uiState.asStateFlow()

    fun loadReminderForTask(taskId: String) {
        viewModelScope.launch {
            _uiState.value = SmartReminderUiState.Loading
            try {
                val stats = getAiUsageStatsUseCase()
                if (stats.reminderCallsToday >= AiUsageLimit.MAX_REMINDER_PER_DAY) {
                    _uiState.value = SmartReminderUiState.QuotaExceeded
                    return@launch
                }

                val schedule = getSmartReminderUseCase(taskId)
                _uiState.value = SmartReminderUiState.Success(schedule)
            } catch (e: Exception) {
                _uiState.value = SmartReminderUiState.Error(e.message ?: "Gagal mendapatkan rekomendasi")
            }
        }
    }
}
