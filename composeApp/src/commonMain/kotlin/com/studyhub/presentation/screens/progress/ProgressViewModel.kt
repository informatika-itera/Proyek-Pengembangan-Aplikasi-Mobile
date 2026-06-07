package com.studyhub.presentation.screens.progress

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyhub.domain.model.Priority
import com.studyhub.domain.model.Task
import com.studyhub.domain.model.TaskStatus
import com.studyhub.domain.repository.PomodoroRepository
import com.studyhub.domain.usecase.task.GetAllTasksUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface ProgressUiState {
    object Loading : ProgressUiState
    data class Success(
        val completedThisWeek: Int,
        val totalThisWeek: Int,
        val completionRate: Float,
        val currentStreak: Int,
        val longestStreak: Int,
        val focusMinutesToday: Int,
        val subjectProgress: List<SubjectProgressData>,
        val highPriorityRate: Float,
        val medPriorityRate: Float,
        val lowPriorityRate: Float
    ) : ProgressUiState
    data class Error(val message: String) : ProgressUiState
    object Empty : ProgressUiState
}

@Stable
data class SubjectProgressData(
    val subject: String,
    val completed: Int,
    val total: Int,
    val completionRate: Float
)

class ProgressViewModel(
    private val getAllTasksUseCase: GetAllTasksUseCase,
    private val pomodoroRepository: PomodoroRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProgressUiState>(ProgressUiState.Loading)
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    fun loadStats() {
        viewModelScope.launch {
            try {
                getAllTasksUseCase().collect { tasks ->
                    if (tasks.isEmpty()) {
                        _uiState.value = ProgressUiState.Empty
                        return@collect
                    }

                    val now = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
                    val startOfWeek = now - (now % 86_400_000L) - (6 * 86_400_000L)

                    val weekTasks = tasks.filter { it.createdAt >= startOfWeek }
                    val completedWeek = weekTasks.count { it.status == TaskStatus.DONE }
                    val rate = if (weekTasks.isNotEmpty()) completedWeek.toFloat() / weekTasks.size else 0f

                    val subjectMap = tasks.groupBy { it.subject }
                    val subjectProgress = subjectMap.map { (subject, list) ->
                        val done = list.count { it.status == TaskStatus.DONE }
                        SubjectProgressData(
                            subject = subject,
                            completed = done,
                            total = list.size,
                            completionRate = if (list.isNotEmpty()) done.toFloat() / list.size else 0f
                        )
                    }.sortedByDescending { it.total }

                    val highTasks = tasks.filter { it.priority == Priority.HIGH }
                    val medTasks = tasks.filter { it.priority == Priority.MEDIUM }
                    val lowTasks = tasks.filter { it.priority == Priority.LOW }

                    val focusMin = try {
                        pomodoroRepository.getTodayFocusMinutes()
                    } catch (e: Exception) { 0 }

                    _uiState.value = ProgressUiState.Success(
                        completedThisWeek = completedWeek,
                        totalThisWeek = weekTasks.size,
                        completionRate = rate,
                        currentStreak = calculateStreak(tasks),
                        longestStreak = calculateStreak(tasks),
                        focusMinutesToday = focusMin,
                        subjectProgress = subjectProgress,
                        highPriorityRate = calcRate(highTasks),
                        medPriorityRate = calcRate(medTasks),
                        lowPriorityRate = calcRate(lowTasks)
                    )
                }
            } catch (e: Exception) {
                _uiState.value = ProgressUiState.Error(e.message ?: "Gagal memuat statistik")
            }
        }
    }

    private fun calcRate(tasks: List<Task>): Float {
        if (tasks.isEmpty()) return 0f
        return tasks.count { it.status == TaskStatus.DONE }.toFloat() / tasks.size
    }

    private fun calculateStreak(tasks: List<Task>): Int {
        val completedDates = tasks
            .filter { it.status == TaskStatus.DONE && it.completedAt != null }
            .map { it.completedAt!! / 86_400_000L }
            .distinct()
            .sortedDescending()

        var streak = 0
        var expectedDay = kotlinx.datetime.Clock.System.now().toEpochMilliseconds() / 86_400_000L

        for (day in completedDates) {
            if (day == expectedDay || day == expectedDay - 1) {
                streak++
                expectedDay = day - 1
            } else break
        }
        return streak
    }
}
