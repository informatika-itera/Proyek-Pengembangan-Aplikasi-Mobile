package com.studyhub.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyhub.domain.model.TaskStatus
import com.studyhub.domain.usecase.preferences.GetUserPreferencesUseCase
import com.studyhub.domain.usecase.preferences.SetDarkModeUseCase
import com.studyhub.domain.usecase.task.GetActiveTasksUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

data class ProfileUiState(
    val userName: String = "Pelajar",
    val isDarkMode: Boolean = false,
    val notificationEnabled: Boolean = true,
    val isAiReminderEnabled: Boolean = true,
    val pomodoroFocusDuration: Int = 25,
    val pomodoroShortBreak: Int = 5,
    val pomodoroLongBreak: Int = 15,
    val totalTasksCompleted: Int = 0,
    val overdueCount: Int = 0,
    val isLoading: Boolean = false
)

class ProfileViewModel(
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val setDarkModeUseCase: SetDarkModeUseCase,
    private val getActiveTasksUseCase: GetActiveTasksUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        observePreferences()
        loadStats()
    }

    private fun observePreferences() {
        viewModelScope.launch(Dispatchers.IO) {
            getUserPreferencesUseCase().collect { prefs ->
                _uiState.update {
                    it.copy(
                        userName = prefs.userName,
                        isDarkMode = prefs.isDarkMode,
                        notificationEnabled = prefs.notificationEnabled,
                        isAiReminderEnabled = prefs.isAiReminderEnabled,
                        pomodoroFocusDuration = prefs.pomodoroFocusDuration,
                        pomodoroShortBreak = prefs.pomodoroShortBreak,
                        pomodoroLongBreak = prefs.pomodoroLongBreak
                    )
                }
            }
        }
    }

    private fun loadStats() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val tasks = getActiveTasksUseCase()
                val now = Clock.System.now().toEpochMilliseconds()
                _uiState.update {
                    it.copy(
                        overdueCount = tasks.count { t ->
                            t.dueDate < now && t.status != TaskStatus.DONE
                        }
                    )
                }
            } catch (e: Exception) {
                // ignore stats error
            }
        }
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            setDarkModeUseCase(!_uiState.value.isDarkMode)
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            setDarkModeUseCase(enabled)
        }
    }
}
