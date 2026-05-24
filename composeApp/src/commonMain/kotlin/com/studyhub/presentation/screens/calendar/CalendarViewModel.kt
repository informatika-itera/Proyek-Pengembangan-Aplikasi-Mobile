package com.studyhub.presentation.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyhub.core.util.toLocalDate
import com.studyhub.domain.model.Task
import com.studyhub.domain.usecase.task.DeleteTaskUseCase
import com.studyhub.domain.usecase.task.GetAllTasksUseCase
import com.studyhub.domain.usecase.task.GetTasksByDateUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class CalendarUiState(
    val selectedDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
    val tasksOnSelectedDate: List<Task> = emptyList(),
    val taskDates: Set<LocalDate> = emptySet(),
    val isLoading: Boolean = false
)

class CalendarViewModel(
    private val getTasksByDateUseCase: GetTasksByDateUseCase,
    private val getAllTasksUseCase: GetAllTasksUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    fun loadAllTaskDates() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val tasks = getAllTasksUseCase()
                val dates = tasks.map { it.dueDate.toLocalDate() }.toSet()
                _uiState.update { it.copy(taskDates = dates) }
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }

    fun selectDate(date: LocalDate) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, selectedDate = date) }
            try {
                val tasks = getTasksByDateUseCase(date)
                _uiState.update { it.copy(isLoading = false, tasksOnSelectedDate = tasks) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteTaskUseCase(taskId)
            loadAllTaskDates()
            selectDate(_uiState.value.selectedDate)
        }
    }
}
