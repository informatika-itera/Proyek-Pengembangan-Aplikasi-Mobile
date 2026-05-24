package com.studyhub.presentation.screens.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyhub.domain.model.Priority
import com.studyhub.domain.model.SortBy
import com.studyhub.domain.model.Task
import com.studyhub.domain.model.TaskStatus
import com.studyhub.domain.usecase.task.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ViewMode { LIST, GRID }

data class TasksUiState(
    val allTasks: List<Task> = emptyList(),
    val filteredTasks: List<Task> = emptyList(),
    val searchQuery: String = "",
    val filterStatus: TaskStatus? = null,
    val filterPriority: Priority? = null,
    val filterSubject: String? = null,
    val sortBy: SortBy = SortBy.DUE_DATE,
    val viewMode: ViewMode = ViewMode.LIST,
    val taskCounts: Map<String, Int> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val deleteConfirmTaskId: String? = null
)

class TasksViewModel(
    private val getAllTasksUseCase: GetAllTasksUseCase,
    private val updateTaskStatusUseCase: UpdateTaskStatusUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val filterSortUseCase: FilterAndSortTasksUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    fun loadTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val tasks = getAllTasksUseCase()
                val counts = mapOf(
                    "all" to tasks.size,
                    TaskStatus.TODO.value to tasks.count { it.status == TaskStatus.TODO },
                    TaskStatus.IN_PROGRESS.value to tasks.count { it.status == TaskStatus.IN_PROGRESS },
                    TaskStatus.DONE.value to tasks.count { it.status == TaskStatus.DONE }
                )
                _uiState.update { it.copy(isLoading = false, allTasks = tasks, taskCounts = counts) }
                applyFilter()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun updateStatus(taskId: String, status: TaskStatus) {
        viewModelScope.launch(Dispatchers.IO) {
            updateTaskStatusUseCase(taskId, status)
            loadTasks()
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteTaskUseCase(taskId)
            loadTasks()
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilter()
    }

    fun setFilter(status: TaskStatus?, priority: Priority?, subject: String?) {
        _uiState.update {
            it.copy(filterStatus = status, filterPriority = priority, filterSubject = subject)
        }
        applyFilter()
    }

    fun setSortBy(sortBy: SortBy) {
        _uiState.update { it.copy(sortBy = sortBy) }
        applyFilter()
    }

    fun toggleViewMode() {
        _uiState.update { 
            it.copy(viewMode = if (it.viewMode == ViewMode.LIST) ViewMode.GRID else ViewMode.LIST) 
        }
    }

    fun clearFilters() {
        _uiState.update {
            it.copy(filterStatus = null, filterPriority = null,
                filterSubject = null, searchQuery = "")
        }
        applyFilter()
    }

    fun showDeleteConfirm(taskId: String?) {
        _uiState.update { it.copy(deleteConfirmTaskId = taskId) }
    }

    private fun applyFilter() {
        val state = _uiState.value
        val filtered = filterSortUseCase(
            state.allTasks, state.filterStatus, state.filterPriority,
            state.filterSubject, state.sortBy, state.searchQuery
        )
        _uiState.update { it.copy(filteredTasks = filtered) }
    }
}
