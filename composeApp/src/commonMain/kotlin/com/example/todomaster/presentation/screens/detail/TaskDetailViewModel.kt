package com.example.todomaster.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todomaster.domain.model.Task
import com.example.todomaster.domain.repository.TaskRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskDetailViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TaskDetailUiState>(TaskDetailUiState.Loading)
    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<TaskDetailEvent>()
    val events: SharedFlow<TaskDetailEvent> = _events.asSharedFlow()

    fun loadTask(taskId: Long) {
        viewModelScope.launch {
            _uiState.value = TaskDetailUiState.Loading
            try {
                val task = repository.getTaskById(taskId)
                _uiState.value = if (task != null) {
                    TaskDetailUiState.Success(task)
                } else {
                    TaskDetailUiState.NotFound
                }
            } catch (e: Exception) {
                _uiState.value = TaskDetailUiState.NotFound
            }
        }
    }

    fun togglePin() {
        val currentState = _uiState.value
        if (currentState is TaskDetailUiState.Success) {
            viewModelScope.launch {
                val updatedTask = currentState.task.copy(isPinned = !currentState.task.isPinned)
                repository.updateTask(updatedTask)
                _uiState.value = TaskDetailUiState.Success(updatedTask)
            }
        }
    }

    fun deleteTask() {
        val currentState = _uiState.value
        if (currentState is TaskDetailUiState.Success) {
            viewModelScope.launch {
                try {
                    repository.deleteTask(currentState.task.id)
                    _events.emit(TaskDetailEvent.TaskDeleted)
                } catch (e: Exception) {
                    _events.emit(TaskDetailEvent.Error(e.message ?: "Gagal menghapus"))
                }
            }
        }
    }

    fun getShareContent(): String? {
        val currentState = _uiState.value
        return if (currentState is TaskDetailUiState.Success) {
            val task = currentState.task
            buildString {
                if (task.title.isNotBlank()) {
                    appendLine("Tugas: ${task.title}")
                    appendLine()
                }
                if (!task.description.isNullOrBlank()) {
                    appendLine("Deskripsi: ${task.description}")
                }
                appendLine("Prioritas: ${task.priority.name.replace("_", " ")}")
            }
        } else null
    }
}

sealed interface TaskDetailUiState {
    data object Loading : TaskDetailUiState
    data class Success(val task: Task) : TaskDetailUiState
    data object NotFound : TaskDetailUiState
}

sealed interface TaskDetailEvent {
    data object TaskDeleted : TaskDetailEvent
    data class Error(val message: String) : TaskDetailEvent
}