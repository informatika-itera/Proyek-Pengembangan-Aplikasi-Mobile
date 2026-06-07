package com.studyhub.presentation.screens.home

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyhub.core.util.atEndOfDayMillis
import com.studyhub.domain.model.Task
import com.studyhub.domain.model.TaskStatus
import com.studyhub.domain.usecase.notification.GetUnreadCountUseCase
import com.studyhub.domain.usecase.preferences.GetUserPreferencesUseCase
import com.studyhub.domain.usecase.task.DeleteTaskUseCase
import com.studyhub.domain.usecase.task.GetActiveTasksUseCase
import com.studyhub.domain.usecase.task.GetAllTasksUseCase
import com.studyhub.domain.usecase.task.GetTasksByDateUseCase
import com.studyhub.domain.usecase.task.UpdateTaskStatusUseCase
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
    data class ShowSnackbar(val message: String) : HomeUiEvent
}

class HomeViewModel(
    private val getActiveTasksUseCase: GetActiveTasksUseCase,
    private val getAllTasksUseCase: GetAllTasksUseCase,
    private val getTasksByDateUseCase: GetTasksByDateUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val updateTaskStatusUseCase: UpdateTaskStatusUseCase,
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val getUnreadCountUseCase: GetUnreadCountUseCase
) : ViewModel() {

    private val now = Clock.System.now()
    private val localNow = now.toLocalDateTime(TimeZone.currentSystemDefault())
    private val today = localNow.date

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()
    
    private val _uiEvent = MutableSharedFlow<HomeUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun loadUnreadCount() {
        viewModelScope.launch {
            try {
                _unreadCount.value = getUnreadCountUseCase()
            } catch (e: Exception) { }
        }
    }

    val uiState: StateFlow<HomeUiState> = combine(
        getUserPreferencesUseCase().distinctUntilChanged(),
        getAllTasksUseCase().distinctUntilChanged(),
        getActiveTasksUseCase().distinctUntilChanged(),
        getTasksByDateUseCase(today).distinctUntilChanged(),
        _unreadCount
    ) { params ->
        val prefs = params[0] as com.studyhub.domain.model.UserPreferences
        val allTasks = params[1] as List<Task>
        val allActive = params[2] as List<Task>
        val todayTasks = params[3] as List<Task>
        val unread = params[4] as Int

        val startOfTomorrow = today.atEndOfDayMillis() + 1
        
        val activeNotDeleted = allTasks.filter { !it.isDeleted }
        val doneCount = activeNotDeleted.count { it.status == TaskStatus.DONE }
        val totalCount = activeNotDeleted.size
        val activeCount = allActive.size
        val dueTodayCount = todayTasks.count { it.status != TaskStatus.DONE }
        
        val completionPct = if (totalCount > 0) (doneCount * 100) / totalCount else 0
        
        val upcoming = allActive
            .filter { it.dueDate >= startOfTomorrow }
            .sortedBy { it.dueDate }
            .take(4)

        val subjects = activeNotDeleted.map { it.subject }.distinct()
        val stats = subjects.map { s ->
            val subTasks = activeNotDeleted.filter { it.subject == s }
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
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState.Loading
    )

    init {
        loadUnreadCount()
    }

    fun updateStatus(taskId: String, status: TaskStatus) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                updateTaskStatusUseCase(taskId, status)
                _uiEvent.emit(HomeUiEvent.ShowSnackbar(
                    if (status == TaskStatus.DONE) "Tugas ditandai selesai ✓" else "Tugas dikembalikan"
                ))
            } catch (e: Exception) {
                _uiEvent.emit(HomeUiEvent.ShowSnackbar("Gagal: ${e.message}"))
            }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                deleteTaskUseCase(taskId)
                _uiEvent.emit(HomeUiEvent.ShowSnackbar("Tugas dihapus"))
            } catch (e: Exception) {
                _uiEvent.emit(HomeUiEvent.ShowSnackbar("Gagal menghapus: ${e.message}"))
            }
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
