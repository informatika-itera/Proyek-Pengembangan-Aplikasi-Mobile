package com.studyhub.presentation.screens.progress

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyhub.domain.model.Priority
import com.studyhub.domain.model.Task
import com.studyhub.domain.model.TaskStatus
import com.studyhub.domain.repository.PomodoroRepository
import com.studyhub.domain.repository.PreferencesRepository
import com.studyhub.domain.usecase.task.GetAllTasksUseCase
import kotlinx.datetime.*
import kotlinx.coroutines.flow.*
import com.studyhub.core.util.atStartOfDayMillis

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
    object Empty : ProgressUiState
    data class Error(val message: String) : ProgressUiState
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
    private val pomodoroRepository: PomodoroRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<ProgressUiState> = combine(
        getAllTasksUseCase(),
        preferencesRepository.userPreferences
    ) { tasks, prefs ->
        try {
            if (tasks.isEmpty()) {
                return@combine ProgressUiState.Empty
            }

            val now = com.studyhub.core.util.currentTimeMillis()
            val startOfWeek = now - (now % 86_400_000L) -
                (6 * 86_400_000L)

            val weekTasks = tasks.filter {
                it.createdAt >= startOfWeek
            }
            val completedWeek = weekTasks.count {
                it.status == TaskStatus.DONE
            }
            val rate = if (weekTasks.isNotEmpty())
                completedWeek.toFloat() / weekTasks.size
            else 0f

            val subjectMap = tasks.groupBy { it.subject }
            val subjectProgress = subjectMap.map { (subject, list) ->
                val done = list.count {
                    it.status == TaskStatus.DONE
                }
                SubjectProgressData(
                    subject = subject,
                    completed = done,
                    total = list.size,
                    completionRate = if (list.isNotEmpty())
                        done.toFloat() / list.size else 0f
                )
            }.sortedByDescending { it.total }

            val highTasks = tasks.filter {
                it.priority == Priority.HIGH
            }
            val medTasks = tasks.filter {
                it.priority == Priority.MEDIUM
            }
            val lowTasks = tasks.filter {
                it.priority == Priority.LOW
            }

            val focusMin = try {
                val today = kotlinx.datetime.Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date
                val startOfToday = today.atStartOfDayMillis()
                pomodoroRepository.getFocusMinutesInRange(startOfToday, startOfToday + 86_400_000L)
            } catch (e: Exception) { 0 }

            ProgressUiState.Success(
                completedThisWeek = completedWeek,
                totalThisWeek = weekTasks.size,
                completionRate = rate,
                currentStreak = prefs.currentStreak,
                longestStreak = prefs.longestStreak,
                focusMinutesToday = focusMin,
                subjectProgress = subjectProgress,
                highPriorityRate = calcRate(highTasks),
                medPriorityRate = calcRate(medTasks),
                lowPriorityRate = calcRate(lowTasks)
            )
        } catch (e: Exception) {
            ProgressUiState.Error(e.message ?: "Gagal memuat statistik")
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProgressUiState.Loading
    )

    fun loadStats() {
        // Now handled by combine + stateIn
    }

    private fun calcRate(tasks: List<Task>): Float {
        if (tasks.isEmpty()) return 0f
        return tasks.count {
            it.status == TaskStatus.DONE
        }.toFloat() / tasks.size
    }
}
