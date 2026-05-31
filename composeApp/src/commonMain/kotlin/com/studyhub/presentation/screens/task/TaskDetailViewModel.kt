package com.studyhub.presentation.screens.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyhub.domain.model.Task
import com.studyhub.domain.usecase.task.DeleteTaskUseCase
import com.studyhub.domain.usecase.task.GetTaskByIdUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class TaskDetailUiState {
    object Loading : TaskDetailUiState()
    data class Success(val task: Task) : TaskDetailUiState()
    data class Error(val message: String) : TaskDetailUiState()
}

sealed class TaskDetailEvent {
    object TaskDeleted : TaskDetailEvent()
}

class TaskDetailViewModel(
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TaskDetailUiState>(TaskDetailUiState.Loading)
    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<TaskDetailEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun loadTask(id: String) {
        viewModelScope.launch {
            _uiState.update { TaskDetailUiState.Loading }
            getTaskByIdUseCase(id).collect { task ->
                if (task != null) {
                    _uiState.update { TaskDetailUiState.Success(task) }
                } else {
                    _uiState.update { TaskDetailUiState.Error("Tugas tidak ditemukan") }
                }
            }
        }
    }

    fun deleteTask(id: String) {
        viewModelScope.launch {
            deleteTaskUseCase(id)
            _eventFlow.emit(TaskDetailEvent.TaskDeleted)
        }
    }
}
