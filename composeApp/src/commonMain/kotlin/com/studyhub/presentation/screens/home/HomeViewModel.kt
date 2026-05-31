package com.studyhub.presentation.screens.home

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyhub.core.util.atEndOfDayMillis
import com.studyhub.domain.model.Task
import com.studyhub.domain.model.TaskStatus
import com.studyhub.domain.model.UserPreferences
import com.studyhub.domain.usecase.preferences.GetUserPreferencesUseCase
import com.studyhub.domain.usecase.task.DeleteTaskUseCase
import com.studyhub.domain.usecase.task.GetActiveTasksUseCase
import com.studyhub.domain.usecase.task.GetAllTasksUseCase
import com.studyhub.domain.usecase.task.GetTasksByDateUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class SubjectHomeStat(
    val name: String,
    val doneCount: Int,
    val totalCount: Int,
    val completionRate: Int,
    val color: Color
)

data class HomeUiState(
    val userName: String = "Pelajar",
    val todayTasksCount: Int = 0,
    val totalTasks: Int = 0,
    val doneTasks: Int = 0,
    val activeTasks: Int = 0,
    val dueTodayTasks: Int = 0,
    val completionPercentage: Int = 0,
    val upcomingTasks: List<Task> = emptyList(),
    val subjectStats: List<SubjectHomeStat> = emptyList(),
    val pomodoroWorkDuration: Int = 25,
    val isLoading: Boolean = false,
    val error: String? = null
)

class HomeViewModel(
    private val getActiveTasksUseCase: GetActiveTasksUseCase,
    private val getAllTasksUseCase: GetAllTasksUseCase,
    private val getTasksByDateUseCase: GetTasksByDateUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase
) : ViewModel() {

    private val now = Clock.System.now()
    private val localNow = now.toLocalDateTime(TimeZone.currentSystemDefault())
    private val today = localNow.date

    // Combine all data sources into a single reactive UI State
    val uiState: StateFlow<HomeUiState> = combine(
        getUserPreferencesUseCase().distinctUntilChanged(),
        getAllTasksUseCase().distinctUntilChanged(),
        getActiveTasksUseCase().distinctUntilChanged(),
        getTasksByDateUseCase(today).distinctUntilChanged()
    ) { prefs, allTasks, allActive, todayTasks ->
        val startOfTomorrow = today.atEndOfDayMillis() + 1
        
        val doneCount = allTasks.count { it.status == TaskStatus.DONE && !it.isDeleted }
        val totalCount = allTasks.count { !it.isDeleted }
        val activeCount = allActive.size
        val dueTodayCount = todayTasks.count { it.status != TaskStatus.DONE }
        
        val completionPct = if (totalCount > 0) (doneCount * 100) / totalCount else 0
        
        val upcoming = allActive
            .filter { it.dueDate >= startOfTomorrow }
            .sortedBy { it.dueDate }
            .take(4)

        val subjects = allTasks.map { it.subject }.distinct()
        val stats = subjects.map { s ->
            val subTasks = allTasks.filter { it.subject == s && !it.isDeleted }
            val subDone = subTasks.count { it.status == TaskStatus.DONE }
            val subTotal = subTasks.size
            SubjectHomeStat(
                name = s,
                doneCount = subDone,
                totalCount = subTotal,
                completionRate = if (subTotal > 0) (subDone * 100) / subTotal else 0,
                color = getSubjectColor(s)
            )
        }.sortedByDescending { it.totalCount }

        HomeUiState(
            userName = prefs.userName,
            pomodoroWorkDuration = prefs.pomodoroFocusDuration,
            todayTasksCount = dueTodayCount,
            totalTasks = totalCount,
            doneTasks = doneCount,
            activeTasks = activeCount,
            dueTodayTasks = dueTodayCount,
            completionPercentage = completionPct,
            upcomingTasks = upcoming,
            subjectStats = stats,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    fun deleteTask(taskId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteTaskUseCase(taskId)
        }
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