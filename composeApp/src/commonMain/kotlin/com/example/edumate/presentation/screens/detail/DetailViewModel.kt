package com.example.edumate.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edumate.domain.model.Task
import com.example.edumate.domain.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailUiState(
    val task: Task? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class DetailViewModel(
    private val repository: TaskRepository,
    private val taskId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadTask()
    }

    private fun loadTask() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Menggunakan collect agar data selalu update secara real-time
                repository.getTaskById(taskId).collect { task ->
                    if (task != null) {
                        _uiState.update { it.copy(task = task, isLoading = false) }
                    } else {
                        _uiState.update { it.copy(error = "Tugas tidak ditemukan", isLoading = false) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Gagal memuat tugas", isLoading = false) }
            }
        }
    }

    fun toggleTaskCompletion() {
        val currentTask = _uiState.value.task ?: return
        viewModelScope.launch {
            try {
                repository.updateTask(currentTask.copy(isCompleted = !currentTask.isCompleted))
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Gagal mengubah status tugas") }
            }
        }
    }

    fun deleteTask(onDeleted: () -> Unit) {
        val currentTask = _uiState.value.task ?: return
        viewModelScope.launch {
            try {
                repository.deleteTask(currentTask.id)
                onDeleted()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Gagal menghapus tugas") }
            }
        }
    }
}