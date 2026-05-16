package com.example.edumate.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edumate.domain.model.Task
import com.example.edumate.domain.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailUiState(
    val isLoading: Boolean = true,
    val task: Task? = null,
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
            val task = repository.getTaskById(taskId).firstOrNull()
            _uiState.update {
                if (task != null) {
                    it.copy(isLoading = false, task = task, error = null)
                } else {
                    it.copy(isLoading = false, task = null, error = "Tugas tidak ditemukan")
                }
            }
        }
    }

    fun deleteTask(onDeleted: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.deleteTask(taskId)
                onDeleted()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Gagal menghapus tugas") }
            }
        }
    }
}
