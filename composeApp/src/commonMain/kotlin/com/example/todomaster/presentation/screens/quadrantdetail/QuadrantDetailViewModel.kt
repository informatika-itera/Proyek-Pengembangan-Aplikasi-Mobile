package com.example.todomaster.presentation.screens.quadrantdetail

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

data class QuadrantDetailUiState(
    val tasks: List<Task> = emptyList(),
    val currentQuadrant: Quadrant = Quadrant.DO_FIRST
)

class QuadrantDetailViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuadrantDetailUiState())
    val uiState: StateFlow<QuadrantDetailUiState> = _uiState.asStateFlow()

    fun setQuadrant(quadrant: Quadrant) {
        viewModelScope.launch {
            repository.getTasksByPriority(quadrant).collectLatest { taskList ->
                _uiState.value = QuadrantDetailUiState(
                    tasks = taskList,
                    currentQuadrant = quadrant
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