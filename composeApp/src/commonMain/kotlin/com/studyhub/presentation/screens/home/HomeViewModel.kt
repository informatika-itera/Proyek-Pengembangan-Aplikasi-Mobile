package com.studyhub.presentation.screens.home

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyhub.core.util.atEndOfDayMillis
import com.studyhub.domain.model.Task
import com.studyhub.domain.model.TaskStatus
import com.studyhub.domain.usecase.notification.ObserveUnreadCountUseCase
import com.studyhub.domain.usecase.preferences.GetUserPreferencesUseCase
import com.studyhub.domain.usecase.task.DeleteTaskUseCase
import com.studyhub.domain.usecase.task.GetActiveTasksUseCase
import com.studyhub.domain.usecase.task.GetAllTasksUseCase
import com.studyhub.domain.usecase.task.GetTasksByDateUseCase
import com.studyhub.domain.repository.PreferencesRepository
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

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(
        val userName: String,
        val todayTasksCount: Int,
        val totalTasks: Int,
        val doneTasks: Int,
        val activeTasks: Int,
        val dueTodayTasks: Int,
        val completionPercentage: Int,
        val upcomingTasks: List<Task>,
        val subjectStats: List<SubjectHomeStat>,
        val pomodoroWorkDuration: Int,
        val unreadNotifCount: Int
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

sealed interface HomeUiEvent {
    data class ShowStreakPopup(val streak: Int) : HomeUiEvent
}

class HomeViewModel(
    private val getActiveTasksUseCase: GetActiveTasksUseCase,
    private val getAllTasksUseCase: GetAllTasksUseCase,
    private val getTasksByDateUseCase: GetTasksByDateUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val observeUnreadCountUseCase: ObserveUnreadCountUseCase,
    private val preferencesRepository: PreferencesRepository,
    private val scheduleSmartReminderUseCase: com.studyhub.domain.usecase.notification.ScheduleSmartReminderUseCase
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<HomeUiEvent>()
    val uiEvent: SharedFlow<HomeUiEvent> = _uiEvent.asSharedFlow()

    private val now = Clock.System.now()
    private val localNow = now.toLocalDateTime(TimeZone.currentSystemDefault())
    private val today = localNow.date

    val uiState: StateFlow<HomeUiState> = combine(
        getUserPreferencesUseCase(),
        getAllTasksUseCase(),
        getActiveTasksUseCase(),
        getTasksByDateUseCase(today),
        observeUnreadCountUseCase()
    ) { prefs, allTasks, allActive, todayTasks, unread ->
        try {
            val startOfTomorrow = today.atEndOfDayMillis() + 1

            val validAllTasks = allTasks.filter { !it.isDeleted }
            val doneCount = validAllTasks.count { it.status == TaskStatus.DONE }
            val totalCount = validAllTasks.size
            val activeCount = allActive.size
            val dueTodayCount = todayTasks.count { it.status != TaskStatus.DONE }

            val completionPct = if (totalCount > 0) (doneCount * 100) / totalCount else 0

            val upcoming = allActive
                .filter { it.dueDate >= startOfTomorrow }
                .sortedBy { it.dueDate }
                .take(4)
                .distinctBy { it.id } // Safety filter for duplicate IDs

            val subjects = validAllTasks.map { it.subject }.distinct()
            val stats = subjects.map { s ->
                val subTasks = validAllTasks.filter { it.subject == s }
                val subDone = subTasks.count { it.status == TaskStatus.DONE }
                val subTotal = subTasks.size
                SubjectHomeStat(
                    name = s,
                    doneCount = subDone,
                    totalCount = subTotal,
                    completionRate = if (subTotal > 0) (subDone * 100) / subTotal else 0,
                    color = Color.Transparent // Will be assigned in UI or kept dynamic
                )
            }.sortedByDescending { it.totalCount }

            HomeUiState.Success(
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
                unreadNotifCount = unread
            )
        } catch (e: Exception) {
            HomeUiState.Error(e.message ?: "Terjadi kesalahan saat memuat data")
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState.Loading
    )

    init {
        updateStreakAndReminders()
    }

    private fun updateStreakAndReminders() {
        viewModelScope.launch {
            try {
                val newStreak = preferencesRepository.updateStreak()
                if (newStreak != null) {
                    _uiEvent.emit(HomeUiEvent.ShowStreakPopup(newStreak))
                }
                
                // Auto schedule reminders for active tasks that don't have one
                val activeTasks = getActiveTasksUseCase().first()
                activeTasks.forEach { task ->
                    scheduleSmartReminderUseCase(task.id)
                }
            } catch (e: Exception) { }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                deleteTaskUseCase(taskId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
