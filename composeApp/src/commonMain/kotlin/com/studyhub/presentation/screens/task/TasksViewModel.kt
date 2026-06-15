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

sealed interface TasksUiState {
    object Loading : TasksUiState
    data class Success(
        val allTasks: List<Task>,
        val filteredTasks: List<Task>,
        val availableSubjects: List<String>,
        val searchQuery: String,
        val filterStatus: TaskStatus?,
        val filterPriority: Priority?,
        val filterSubject: String?,
        val sortBy: SortBy,
        val showCompleted: Boolean,
        val viewMode: ViewMode,
        val taskCounts: Map<String, Int>,
        val deleteConfirmTaskId: String? = null
    ) : TasksUiState
    data class Error(val message: String) : TasksUiState
}

sealed interface UiEvent {
    data class ShowSnackbar(
        val message: String,
        val actionLabel: String? = null
    ) : UiEvent
    object NavigateBack : UiEvent
}

class TasksViewModel(
    private val getAllTasksUseCase: GetAllTasksUseCase,
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

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    val uiState: StateFlow<TasksUiState> = combine(
        getAllTasksUseCase(),
        _searchQuery,
        _filterStatus,
        _filterPriority,
        _filterSubject,
        _sortBy,
        _showCompleted,
        _viewMode,
        _deleteConfirmTaskId
    ) { flows ->
        try {
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
            ).distinctBy { it.id } // Safety filter for duplicate IDs
            
            val counts = mapOf(
                "all" to allTasks.size,
                TaskStatus.TODO.value to allTasks.count { it.status == TaskStatus.TODO },
                TaskStatus.IN_PROGRESS.value to allTasks.count { it.status == TaskStatus.IN_PROGRESS },
                TaskStatus.DONE.value to allTasks.count { it.status == TaskStatus.DONE }
            )

            val subjects = allTasks.map { it.subject }.distinct().sorted()

            TasksUiState.Success(
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
                deleteConfirmTaskId = deleteId
            )
        } catch (e: Exception) {
            TasksUiState.Error(e.message ?: "Terjadi kesalahan saat memuat tugas")
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TasksUiState.Loading
    )

    fun deleteTask(taskId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                deleteTaskUseCase(taskId)
                _uiEvent.emit(UiEvent.ShowSnackbar("Tugas dihapus"))
            } catch (e: Exception) { }
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
