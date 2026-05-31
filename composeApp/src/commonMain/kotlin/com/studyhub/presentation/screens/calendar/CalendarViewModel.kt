package com.studyhub.presentation.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyhub.core.util.atEndOfDayMillis
import com.studyhub.core.util.toLocalDate
import com.studyhub.domain.model.Task
import com.studyhub.domain.model.TaskStatus
import com.studyhub.domain.usecase.task.DeleteTaskUseCase
import com.studyhub.domain.usecase.task.GetAllTasksUseCase
import com.studyhub.domain.usecase.task.GetTasksByDateUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class CalendarUiState(
    val selectedDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
    val tasksOnSelectedDate: List<Task> = emptyList(),
    val taskDates: Set<LocalDate> = emptySet(),
    val upcomingMonthTasks: List<Task> = emptyList(),
    val upcomingDeadlinesCount: Int = 0,
    val allTasks: List<Task> = emptyList(),
    val isLoading: Boolean = false
)

class CalendarViewModel(
    private val getTasksByDateUseCase: GetTasksByDateUseCase,
    private val getAllTasksUseCase: GetAllTasksUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)
    private val _currentMonth = MutableStateFlow(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<CalendarUiState> = combine(
        getAllTasksUseCase().distinctUntilChanged(),
        _selectedDate.flatMapLatest { getTasksByDateUseCase(it) }.distinctUntilChanged(),
        _selectedDate,
        _currentMonth
    ) { allTasks, tasksOnDate, selectedDate, currentMonth ->
        
        val datesWithTasks = allTasks.map { it.dueDate.toLocalDate() }.toSet()
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

        CalendarUiState(
            selectedDate = selectedDate,
            tasksOnSelectedDate = tasksOnDate,
            taskDates = datesWithTasks,
            upcomingMonthTasks = monthTasks,
            upcomingDeadlinesCount = upcomingCount,
            allTasks = allTasks,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CalendarUiState(isLoading = true)
    )

    fun updateMonthOverview(month: LocalDate) {
        _currentMonth.value = month
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteTaskUseCase(taskId)
        }
    }
}
