package com.studyhub.presentation.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyhub.core.util.atEndOfDayMillis
import com.studyhub.core.util.atStartOfDayMillis
import com.studyhub.core.util.toLocalDate
import com.studyhub.domain.model.Task
import com.studyhub.domain.model.TaskStatus
import com.studyhub.domain.usecase.task.DeleteTaskUseCase
import com.studyhub.domain.usecase.task.GetAllTasksUseCase
import com.studyhub.domain.usecase.task.GetTasksByDateUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*

sealed interface CalendarUiState {
    object Loading : CalendarUiState
    data class Success(
        val selectedDate: LocalDate,
        val tasksOnSelectedDate: List<Task>,
        val taskDates: Set<LocalDate>,
        val upcomingMonthTasks: List<Task>,
        val upcomingDeadlinesCount: Int,
        val allTasks: List<Task>
    ) : CalendarUiState
    data class Error(val message: String) : CalendarUiState
}

sealed interface CalendarUiEvent {
    data class ShowSnackbar(val message: String) : CalendarUiEvent
}

class CalendarViewModel(
    private val getTasksByDateUseCase: GetTasksByDateUseCase,
    private val getAllTasksUseCase: GetAllTasksUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)
    private val _currentMonth = MutableStateFlow(_selectedDate.value.let { LocalDate(it.year, it.month, 1) })
    
    private val _uiEvent = MutableSharedFlow<CalendarUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    val uiState: StateFlow<CalendarUiState> = combine(
        _selectedDate,
        _currentMonth,
        getAllTasksUseCase()
    ) { date, month, allTasks ->
        val activeTasks = allTasks.filter { !it.isDeleted }
        val tasksOnDate = activeTasks.filter { it.dueDate.toLocalDate() == date }
        
        val taskDates = activeTasks.map { it.dueDate.toLocalDate() }.toSet()

        val nextMonth = if (month.monthNumber == 12) LocalDate(month.year + 1, 1, 1) else LocalDate(month.year, month.monthNumber + 1, 1)
        val monthEnd = nextMonth.atStartOfDayMillis() - 1
        val monthStart = month.atStartOfDayMillis()
        
        val upcomingMonth = activeTasks.filter { it.dueDate in monthStart..monthEnd }.sortedBy { it.dueDate }
        
        val now = Clock.System.now().toEpochMilliseconds()
        val overdueCount = activeTasks.count { it.dueDate < now && it.status != TaskStatus.DONE }

        CalendarUiState.Success(
            selectedDate = date,
            tasksOnSelectedDate = tasksOnDate,
            taskDates = taskDates,
            upcomingMonthTasks = upcomingMonth,
            upcomingDeadlinesCount = overdueCount,
            allTasks = activeTasks
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CalendarUiState.Loading
    )

    fun updateMonthOverview(month: LocalDate) {
        _currentMonth.value = month
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                deleteTaskUseCase(taskId)
                _uiEvent.emit(CalendarUiEvent.ShowSnackbar("Tugas berhasil dihapus"))
            } catch (e: Exception) {
                _uiEvent.emit(CalendarUiEvent.ShowSnackbar("Gagal menghapus: ${e.message}"))
            }
        }
    }
}
