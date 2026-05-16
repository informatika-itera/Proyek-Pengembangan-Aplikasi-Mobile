package com.example.todomaster.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todomaster.domain.model.Quadrant
import com.example.todomaster.domain.model.Task
import com.example.todomaster.domain.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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

    init {
        viewModelScope.launch {
            repository.getAllTasks().collectLatest { allTasks ->
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
}