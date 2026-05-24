package com.example.todomaster.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todomaster.domain.model.Quadrant
import com.example.todomaster.domain.model.Task
import com.example.todomaster.domain.repository.TaskRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

data class HomeUiState(
    val doFirstTasks: List<Task> = emptyList(),
    val scheduleTasks: List<Task> = emptyList(),
    val delegateTasks: List<Task> = emptyList(),
    val dontDoTasks: List<Task> = emptyList()
)

class HomeViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _allTasks = MutableStateFlow<List<Task>>(emptyList())

    @OptIn(FlowPreview::class)
    val filteredTasks: StateFlow<List<Task>> = combine(_allTasks, _searchQuery.debounce(300L)) { tasks, query ->
        if (query.isBlank()) {
            tasks
        } else {
            tasks.filter {
                it.title.contains(query, ignoreCase = true) ||
                        (it.description?.contains(query, ignoreCase = true) == true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            repository.getAllTasks().collectLatest { allTasks ->
                _allTasks.value = allTasks
                _uiState.value = HomeUiState(
                    doFirstTasks = allTasks.filter { it.priority == Quadrant.DO_FIRST },
                    scheduleTasks = allTasks.filter { it.priority == Quadrant.SCHEDULE },
                    delegateTasks = allTasks.filter { it.priority == Quadrant.DELEGATE },
                    dontDoTasks = allTasks.filter { it.priority == Quadrant.DONT_DO }
                )
            }
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(task.id, !task.isCompleted)
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun changeTab(index: Int) {
        _currentTab.value = index
    }
}