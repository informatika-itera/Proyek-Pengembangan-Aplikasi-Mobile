package com.studyhub.presentation.screens.task

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyhub.domain.model.Priority
import com.studyhub.domain.model.SortBy
import com.studyhub.domain.model.Task
import com.studyhub.domain.model.TaskStatus
import com.studyhub.domain.usecase.task.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class ViewMode { LIST, GRID }

@Stable
data class TasksUiState(
    val allTasks: List<Task> = emptyList(),
    val filteredTasks: List<Task> = emptyList(),
    val availableSubjects: List<String> = emptyList(),
    val searchQuery: String = "",
    val filterStatus: TaskStatus? = null,
    val filterPriority: Priority? = null,
    val filterSubject: String? = null,
    val sortBy: SortBy = SortBy.DUE_DATE,
    val showCompleted: Boolean = false,
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

    private val _searchQuery = MutableStateFlow("")
    private val _filterStatus = MutableStateFlow<TaskStatus?>(null)
    private val _filterPriority = MutableStateFlow<Priority?>(null)
    private val _filterSubject = MutableStateFlow<String?>(null)
    private val _sortBy = MutableStateFlow(SortBy.DUE_DATE)
    private val _showCompleted = MutableStateFlow(false)
    private val _viewMode = MutableStateFlow(ViewMode.LIST)
    private val _deleteConfirmTaskId = MutableStateFlow<String?>(null)

    val uiState: StateFlow<TasksUiState> = combine(
        getAllTasksUseCase().distinctUntilChanged(),
        _searchQuery,
        _filterStatus,
        _filterPriority,
        _filterSubject,
        _sortBy,
        _showCompleted,
        _viewMode,
        _deleteConfirmTaskId
    ) { flows ->
        val allTasks = flows[0] as List<Task>
        val query = flows[1] as String
        val status = flows[2] as TaskStatus?
        val priority = flows[3] as Priority?
        val subject = flows[4] as String?
        val sortBy = flows[5] as SortBy
        val showCompleted = flows[6] as Boolean
        val viewMode = flows[7] as ViewMode
        val deleteId = flows[8] as String?

        val filtered = filterSortUseCase(
            allTasks, status, priority, subject, sortBy, query, showCompleted
        )
        
        val counts = mapOf(
            "all" to allTasks.size,
            TaskStatus.TODO.value to allTasks.count { it.status == TaskStatus.TODO },
            TaskStatus.IN_PROGRESS.value to allTasks.count { it.status == TaskStatus.IN_PROGRESS },
            TaskStatus.DONE.value to allTasks.count { it.status == TaskStatus.DONE }
        )

        val subjects = allTasks.map { it.subject }.distinct().sorted()

        TasksUiState(
            allTasks = allTasks,
            filteredTasks = filtered,
            availableSubjects = subjects,
            searchQuery = query,
            filterStatus = status,
            filterPriority = priority,
            filterSubject = subject,
            sortBy = sortBy,
            showCompleted = showCompleted,
            viewMode = viewMode,
            taskCounts = counts,
            isLoading = false,
            deleteConfirmTaskId = deleteId
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TasksUiState(isLoading = true)
    )

    fun updateStatus(taskId: String, status: TaskStatus) {
        viewModelScope.launch(Dispatchers.IO) {
            updateTaskStatusUseCase(taskId, status)
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteTaskUseCase(taskId)
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilter(status: TaskStatus?, priority: Priority?, subject: String?) {
        _filterStatus.value = status
        _filterPriority.value = priority
        _filterSubject.value = subject
    }

    fun setSubjectFilter(subject: String?) {
        _filterSubject.value = subject
    }

    fun toggleShowCompleted() {
        _showCompleted.value = !_showCompleted.value
    }

    fun setSortBy(sortBy: SortBy) {
        _sortBy.value = sortBy
    }

    fun toggleViewMode() {
        _viewMode.update { if (it == ViewMode.LIST) ViewMode.GRID else ViewMode.LIST }
    }

    fun clearFilters() {
        _filterStatus.value = null
        _filterPriority.value = null
        _filterSubject.value = null
        _searchQuery.value = ""
        _showCompleted.value = false
    }

    fun showDeleteConfirm(taskId: String?) {
        _deleteConfirmTaskId.value = taskId
    }
}
