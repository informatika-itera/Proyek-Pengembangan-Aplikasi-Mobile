package com.studyhub.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyhub.domain.model.TaskStatus
import com.studyhub.domain.repository.PreferencesRepository
import com.studyhub.domain.repository.ReminderRepository
import com.studyhub.domain.usecase.notification.CancelReminderUseCase
import com.studyhub.domain.usecase.preferences.GetUserPreferencesUseCase
import com.studyhub.domain.usecase.preferences.SetDarkModeUseCase
import com.studyhub.domain.usecase.task.GetAllTasksUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface ProfileUiState {
    object Loading : ProfileUiState
    data class Success(
        val userName: String,
        val major: String,
        val level: Int,
        val isDarkMode: Boolean,
        val notificationEnabled: Boolean,
        val isAiReminderEnabled: Boolean,
        val pomodoroFocusDuration: Int,
        val pomodoroShortBreak: Int,
        val pomodoroLongBreak: Int,
        val totalTasks: Int,
        val doneTasks: Int,
        val inProgressTasks: Int,
        val completionRate: Int,
        val totalStudyHours: Int,
        val subjectBreakdown: List<SubjectStat>,
        val achievements: List<Achievement>,
        val dayStreak: Int
    ) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

@androidx.compose.runtime.Stable
data class SubjectStat(
    val name: String,
    val count: Int
)

@androidx.compose.runtime.Stable
data class Achievement(
    val title: String,
    val isEarned: Boolean,
    val type: String
)

class ProfileViewModel(
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val setDarkModeUseCase: SetDarkModeUseCase,
    private val getAllTasksUseCase: GetAllTasksUseCase,
    private val preferencesRepository: PreferencesRepository,
    private val reminderRepository: ReminderRepository
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = combine(
        getUserPreferencesUseCase(),
        getAllTasksUseCase()
    ) { prefs, allTasks ->
        try {
            val doneCount = allTasks.count { it.status == TaskStatus.DONE }
            val inProgressCount = allTasks.count { it.status == TaskStatus.IN_PROGRESS }
            val totalCount = allTasks.size
            val completionRate = if (totalCount > 0) (doneCount * 100) / totalCount else 0
            
            val totalMinutes = allTasks.filter { it.status == TaskStatus.DONE }.sumOf { it.estimatedMinutes }
            val totalHours = totalMinutes / 60

            val subjects = allTasks.map { it.subject }.distinct()
            val subjectBreakdown = subjects.map { s ->
                SubjectStat(
                    name = s,
                    count = allTasks.count { it.subject == s }
                )
            }

            val achievements = listOf(
                Achievement("7-Day Streak", true, "Static"),
                Achievement("Task Master", doneCount >= 10, "Dinamis"),
                Achievement("Speed Learner", false, "Static"),
                Achievement("Perfect Week", completionRate == 100 && totalCount > 0, "Dinamis")
            )

            ProfileUiState.Success(
                userName = prefs.userName,
                major = "Computer Science",
                level = 12,
                isDarkMode = prefs.isDarkMode,
                notificationEnabled = prefs.notificationEnabled,
                isAiReminderEnabled = prefs.isAiReminderEnabled,
                pomodoroFocusDuration = prefs.pomodoroFocusDuration,
                pomodoroShortBreak = prefs.pomodoroShortBreak,
                pomodoroLongBreak = prefs.pomodoroLongBreak,
                totalTasks = totalCount,
                doneTasks = doneCount,
                inProgressTasks = inProgressCount,
                completionRate = completionRate,
                totalStudyHours = totalHours,
                subjectBreakdown = subjectBreakdown,
                achievements = achievements,
                dayStreak = prefs.currentStreak
            )
        } catch (e: Exception) {
            ProfileUiState.Error(e.message ?: "Terjadi kesalahan")
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState.Loading
    )

    fun toggleDarkMode() {
        viewModelScope.launch {
            val state = uiState.value
            if (state is ProfileUiState.Success) {
                setDarkModeUseCase(!state.isDarkMode)
            }
        }
    }

    fun toggleNotification(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setNotificationEnabled(enabled)
            if (!enabled) {
                try {
                    val cancelAll = CancelReminderUseCase(reminderRepository)
                    cancelAll.cancelAll()
                } catch (e: Exception) { }
            }
        }
    }

    fun toggleAiReminder(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setAiReminderEnabled(enabled)
        }
    }

    fun updatePomodoroFocus(delta: Int) {
        viewModelScope.launch {
            val state = uiState.value
            if (state is ProfileUiState.Success) {
                val newValue = (state.pomodoroFocusDuration + delta).coerceIn(5, 60)
                preferencesRepository.setPomodoroSettings(
                    focus = newValue,
                    shortBreak = state.pomodoroShortBreak,
                    longBreak = state.pomodoroLongBreak
                )
            }
        }
    }

    fun updatePomodoroBreak(delta: Int) {
        viewModelScope.launch {
            val state = uiState.value
            if (state is ProfileUiState.Success) {
                val newValue = (state.pomodoroShortBreak + delta).coerceIn(1, 30)
                preferencesRepository.setPomodoroSettings(
                    focus = state.pomodoroFocusDuration,
                    shortBreak = newValue,
                    longBreak = state.pomodoroLongBreak
                )
            }
        }
    }
}
