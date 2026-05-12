package com.example.edumate.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edumate.domain.model.Task
import com.example.edumate.domain.repository.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = repository.getAllTasks()
        .map { tasks ->
            if (tasks.isEmpty()) {
                HomeUiState.Empty
            } else {
                HomeUiState.Success(tasks)
            }
        }
        .catch { e ->
            emit(HomeUiState.Error(e.message ?: "Terjadi kesalahan sistem"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState.Loading
        )

    fun toggleTaskComplete(taskId: Long) {
        viewModelScope.launch {
            repository.toggleComplete(taskId)
        }
    }
}

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val tasks: List<Task>) : HomeUiState
    data object Empty : HomeUiState
    data class Error(val message: String) : HomeUiState
}