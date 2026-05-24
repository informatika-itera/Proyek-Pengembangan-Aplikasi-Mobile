package com.studyhub.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyhub.core.util.atEndOfDayMillis
import com.studyhub.core.util.atStartOfDayMillis
import com.studyhub.domain.model.Task
import com.studyhub.domain.model.TaskStatus
import com.studyhub.domain.usecase.task.DeleteTaskUseCase
import com.studyhub.domain.usecase.task.GetActiveTasksUseCase
import com.studyhub.domain.usecase.task.GetTasksByDateUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class HomeUiState(
    val todayTasks: List<Task> = emptyList(),
    val upcomingTasks: List<Task> = emptyList(),
    val completedThisWeek: Int = 0,
    val overdueCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

class HomeViewModel(
    private val getActiveTasksUseCase: GetActiveTasksUseCase,
    private val getTasksByDateUseCase: GetTasksByDateUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                val today = now.date
                val todayTasks = getTasksByDateUseCase(today)
                val allActive = getActiveTasksUseCase()
                val startOfTomorrow = today.atEndOfDayMillis() + 1
                
                val upcoming = allActive.filter { it.dueDate >= startOfTomorrow }
                    .take(5)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        todayTasks = todayTasks,
                        upcomingTasks = upcoming,
                        overdueCount = allActive.count {
                            t -> t.dueDate < Clock.System.now().toEpochMilliseconds() && t.status != TaskStatus.DONE
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteTaskUseCase(taskId)
            loadData()
        }
    }
}
