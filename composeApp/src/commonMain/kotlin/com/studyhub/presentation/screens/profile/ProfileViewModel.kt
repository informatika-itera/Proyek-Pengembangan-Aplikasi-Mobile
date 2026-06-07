package com.studyhub.presentation.screens.profile

import androidx.compose.ui.graphics.Color
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
        val major: String = "Computer Science",
        val level: Int = 12,
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
        val achievements: List<Achievement>
    ) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

data class SubjectStat(
    val name: String,
    val count: Int,
    val color: Color
)

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
            val activeTasks = allTasks.filter { !it.isDeleted }
            val doneCount = activeTasks.count { it.status == TaskStatus.DONE }
            val inProgressCount = activeTasks.count { it.status == TaskStatus.IN_PROGRESS }
            val totalCount = activeTasks.size
            val completionRate = if (totalCount > 0) (doneCount * 100) / totalCount else 0
            
            val totalMinutes = activeTasks.filter { it.status == TaskStatus.DONE }.sumOf { it.estimatedMinutes }
            val totalHours = totalMinutes / 60

            val subjects = activeTasks.map { it.subject }.distinct()
            val subjectBreakdown = subjects.map { s ->
                SubjectStat(
                    name = s,
                    count = activeTasks.count { it.subject == s },
                    color = getSubjectColor(s)
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
                achievements = achievements
            )
        } catch (e: Exception) {
            ProfileUiState.Error(e.message ?: "Terjadi kesalahan")
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly, // Change to Eagerly for testing
        initialValue = ProfileUiState.Loading
    )

    fun toggleDarkMode() {
        viewModelScope.launch {
            val current = (uiState.value as? ProfileUiState.Success)?.isDarkMode ?: false
            setDarkModeUseCase(!current)
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
        // Implementation logic
    }

    fun updatePomodoroBreak(delta: Int) {
        // Implementation logic
    }
}

private fun getSubjectColor(subject: String): Color {
    val hash = subject.hashCode()
    val colors = listOf(
        Color(0xFF7B6FA0), Color(0xFF6B8F71), Color(0xFF8B7355),
        Color(0xFFC06C84), Color(0xFF355C7D), Color(0xFFF67280), Color(0xFF45B7D1)
    )
    return colors[kotlin.math.abs(hash) % colors.size]
}
