package com.studyhub.presentation.screens.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyhub.domain.model.Priority
import com.studyhub.domain.model.SortBy
import com.studyhub.domain.model.Task
import com.studyhub.domain.model.TaskStatus
import com.studyhub.domain.usecase.task.DeleteTaskUseCase
import com.studyhub.domain.usecase.task.FilterAndSortTasksUseCase
import com.studyhub.domain.usecase.task.GetAllTasksUseCase
import com.studyhub.domain.usecase.task.UpdateTaskStatusUseCase
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
        val searchQuery: String = "",
        val filterStatus: TaskStatus? = null,
        val filterPriority: Priority? = null,
        val filterSubject: String? = null,
        val sortBy: SortBy = SortBy.DUE_DATE,
        val showCompleted: Boolean = false,
        val viewMode: ViewMode = ViewMode.LIST,
        val taskCounts: Map<String, Int> = emptyMap(),
        val deleteConfirmTaskId: String? = null
    ) : TasksUiState
    data class Error(val message: String) : TasksUiState
}

sealed interface TasksUiEvent {
    data class ShowSnackbar(val message: String, val actionLabel: String? = null) : TasksUiEvent
}

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
    
    private val _uiEvent = MutableSharedFlow<TasksUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

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
    ) { params ->
        val allTasks = params[0] as List<Task>
        val query = params[1] as String
        val status = params[2] as TaskStatus?
        val priority = params[3] as Priority?
        val subject = params[4] as String?
        val sort = params[5] as SortBy
        val showComp = params[6] as Boolean
        val vMode = params[7] as ViewMode
        val delId = params[8] as String?

        val activeTasks = allTasks.filter { !it.isDeleted }
        val filtered = filterSortUseCase(
            tasks = activeTasks,
            searchQuery = query,
            filterStatus = status,
            filterPriority = priority,
            filterSubject = subject,
            sortBy = sort,
            showCompleted = showComp
        )

        val subjects = activeTasks.map { it.subject }.distinct().sorted()
        
        val counts = mutableMapOf<String, Int>()
        counts["all"] = activeTasks.size
        TaskStatus.entries.forEach { s ->
            counts[s.value] = activeTasks.count { it.status == s }
        }

        TasksUiState.Success(
            allTasks = activeTasks,
            filteredTasks = filtered,
            availableSubjects = subjects,
            searchQuery = query,
            filterStatus = status,
            filterPriority = priority,
            filterSubject = subject,
            sortBy = sort,
            showCompleted = showComp,
            viewMode = vMode,
            taskCounts = counts,
            deleteConfirmTaskId = delId
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TasksUiState.Loading
    )

    fun updateStatus(taskId: String, status: TaskStatus) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                updateTaskStatusUseCase(taskId, status)
                _uiEvent.emit(TasksUiEvent.ShowSnackbar(
                    if (status == TaskStatus.DONE) "Tugas ditandai selesai ✓" else "Tugas dikembalikan ke daftar"
                ))
            } catch (e: Exception) {
                _uiEvent.emit(TasksUiEvent.ShowSnackbar("Gagal memperbarui status: ${e.message}"))
            }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                deleteTaskUseCase(taskId)
                _uiEvent.emit(TasksUiEvent.ShowSnackbar("Tugas dihapus"))
            } catch (e: Exception) {
                _uiEvent.emit(TasksUiEvent.ShowSnackbar("Gagal menghapus tugas: ${e.message}"))
            }
        }
    }

    fun setSearchQuery(query: String) { _searchQuery.value = query }

    fun setFilter(status: TaskStatus?, priority: Priority?, subject: String?) {
        _filterStatus.value = status
        _filterPriority.value = priority
        _filterSubject.value = subject
    }

    fun setSubjectFilter(subject: String?) { _filterSubject.value = subject }

    fun toggleShowCompleted() { _showCompleted.value = !_showCompleted.value }

    fun setSortBy(sortBy: SortBy) { _sortBy.value = sortBy }

    fun toggleViewMode() {
        _viewMode.value = if (_viewMode.value == ViewMode.LIST) ViewMode.GRID else ViewMode.LIST
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _filterStatus.value = null
        _filterPriority.value = null
        _filterSubject.value = null
    }

    fun showDeleteConfirm(taskId: String?) { _deleteConfirmTaskId.value = taskId }
}
