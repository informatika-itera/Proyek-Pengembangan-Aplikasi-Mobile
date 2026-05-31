package com.studyhub.presentation.screens.profile

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyhub.domain.model.TaskStatus
import com.studyhub.domain.usecase.preferences.GetUserPreferencesUseCase
import com.studyhub.domain.usecase.preferences.SetDarkModeUseCase
import com.studyhub.domain.usecase.task.GetAllTasksUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProfileUiState(
    val userName: String = "Pelajar",
    val major: String = "Computer Science",
    val level: Int = 12,
    val isDarkMode: Boolean = false,
    val notificationEnabled: Boolean = true,
    val isAiReminderEnabled: Boolean = true,
    val pomodoroFocusDuration: Int = 25,
    val pomodoroShortBreak: Int = 5,
    val pomodoroLongBreak: Int = 15,
    val totalTasks: Int = 0,
    val doneTasks: Int = 0,
    val inProgressTasks: Int = 0,
    val completionRate: Int = 0,
    val totalStudyHours: Int = 0,
    val subjectBreakdown: List<SubjectStat> = emptyList(),
    val achievements: List<Achievement> = emptyList(),
    val isLoading: Boolean = false
)

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
    private val getAllTasksUseCase: GetAllTasksUseCase
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = combine(
        getUserPreferencesUseCase(),
        getAllTasksUseCase()
    ) { prefs, allTasks ->
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
                count = allTasks.count { it.subject == s },
                color = getSubjectColor(s)
            )
        }

        val achievements = listOf(
            Achievement("7-Day Streak", true, "Static"),
            Achievement("Task Master", doneCount >= 10, "Dinamis"),
            Achievement("Speed Learner", false, "Static"),
            Achievement("Perfect Week", completionRate == 100 && totalCount > 0, "Dinamis")
        )

        ProfileUiState(
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
            achievements = achievements,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState(isLoading = true)
    )

    fun toggleDarkMode() {
        viewModelScope.launch {
            setDarkModeUseCase(!uiState.value.isDarkMode)
        }
    }

    fun updatePomodoroFocus(delta: Int) {
        // TODO: Implement preference update
    }

    fun updatePomodoroBreak(delta: Int) {
        // TODO: Implement preference update
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
