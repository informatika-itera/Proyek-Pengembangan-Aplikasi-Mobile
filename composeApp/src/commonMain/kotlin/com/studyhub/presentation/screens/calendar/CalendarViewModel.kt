package com.studyhub.presentation.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyhub.core.util.toLocalDate
import com.studyhub.domain.model.Task
import com.studyhub.domain.model.TaskStatus
import com.studyhub.domain.usecase.task.DeleteTaskUseCase
import com.studyhub.domain.usecase.task.GetAllTasksUseCase
import com.studyhub.domain.usecase.task.GetTasksByDateUseCase
import com.studyhub.core.util.TaskColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

sealed interface CalendarUiState {
    object Loading : CalendarUiState
    data class Success(
        val selectedDate: LocalDate,
        val tasksOnSelectedDate: List<Task>,
        val calendarDays: Map<LocalDate, List<String>>, // Date to colorHex list
        val upcomingMonthTasks: List<Task>,
        val upcomingDeadlinesCount: Int
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
    private val _currentMonth = MutableStateFlow(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)

    private val _uiEvent = MutableSharedFlow<CalendarUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _uiState = MutableStateFlow<CalendarUiState>(CalendarUiState.Loading)
    val uiState: StateFlow<CalendarUiState> = combine(
        getAllTasksUseCase(),
        _selectedDate.flatMapLatest { getTasksByDateUseCase(it) },
        _selectedDate,
        _currentMonth
    ) { allTasks, tasksOnDate, selectedDate, currentMonth ->
        try {
            val dayMap = mutableMapOf<LocalDate, MutableList<String>>()
            allTasks.filter { !it.isDeleted }.forEach { task ->
                val date = task.dueDate.toLocalDate()
                val colors = dayMap.getOrPut(date) { mutableListOf() }
                if (colors.size < 3) {
                    colors.add(task.colorHex)
                }
            }

            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

            val upcomingCount = allTasks.count {
                it.status != TaskStatus.DONE && it.dueDate.toLocalDate() >= today
            }

            val monthTasks = allTasks.filter {
                val date = it.dueDate.toLocalDate()
                date.month == currentMonth.month &&
                date.year == currentMonth.year &&
                it.status != TaskStatus.DONE
            }.sortedBy { it.dueDate }

            CalendarUiState.Success(
                selectedDate = selectedDate,
                tasksOnSelectedDate = tasksOnDate,
                calendarDays = dayMap,
                upcomingMonthTasks = monthTasks,
                upcomingDeadlinesCount = upcomingCount
            )
        } catch (e: Exception) {
            CalendarUiState.Error(e.message ?: "Terjadi kesalahan saat memuat kalender")
        }
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
                _uiEvent.emit(CalendarUiEvent.ShowSnackbar("Tugas dihapus"))
            } catch (e: Exception) {
                // Silently fail or log for UI feedback if needed
            }
        }
    }
}
