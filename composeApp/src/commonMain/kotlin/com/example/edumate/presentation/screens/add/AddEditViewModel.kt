package com.example.edumate.presentation.screens.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edumate.domain.model.Task
import com.example.edumate.domain.model.TaskPriority
import com.example.edumate.domain.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddEditUiState(
    val title: String = "",
    val description: String = "",
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

class AddEditViewModel(
    private val repository: TaskRepository,
    private val taskId: Long?
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditUiState())
    val uiState: StateFlow<AddEditUiState> = _uiState.asStateFlow()

    private var currentTask: Task? = null

    init {
        if (taskId != null) {
            loadTask(taskId)
        }
    }

    private fun loadTask(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val task = repository.getTaskById(id).firstOrNull()
            if (task != null) {
                currentTask = task
                _uiState.update {
                    it.copy(
                        title = task.title,
                        description = task.description,
                        priority = task.priority,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Tugas tidak ditemukan") }
            }
        }
    }

    fun onEvent(event: AddEditEvent) {
        when (event) {
            is AddEditEvent.EnteredTitle -> _uiState.update { it.copy(title = event.value) }
            is AddEditEvent.EnteredDescription -> _uiState.update { it.copy(description = event.value) }
            is AddEditEvent.SelectedPriority -> _uiState.update { it.copy(priority = event.value) }
            is AddEditEvent.SaveTask -> saveTask()
        }
    }

    private fun saveTask() {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.update { it.copy(error = "Judul tidak boleh kosong") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                if (currentTask != null) {
                    repository.updateTask(
                        currentTask!!.copy(
                            title = state.title,
                            description = state.description,
                            priority = state.priority
                        )
                    )
                } else {
                    repository.insertTask(
                        Task(
                            title = state.title,
                            description = state.description,
                            priority = state.priority
                        )
                    )
                }
                _uiState.update { it.copy(isSaved = true, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }
}

sealed interface AddEditEvent {
    data class EnteredTitle(val value: String) : AddEditEvent
    data class EnteredDescription(val value: String) : AddEditEvent
    data class SelectedPriority(val value: TaskPriority) : AddEditEvent
    data object SaveTask : AddEditEvent
}