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
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

data class AddEditUiState(
    val title: String = "",
    val description: String = "",
    val deadlineText: String = "",
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
            _uiState.update { it.copy(isLoading = true, error = null) }
            val task = repository.getTaskById(id).firstOrNull()
            if (task != null) {
                currentTask = task
                _uiState.update {
                    it.copy(
                        title = task.title,
                        description = task.description,
                        deadlineText = task.deadline?.toDateText().orEmpty(),
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
            is AddEditEvent.EnteredTitle -> _uiState.update { it.copy(title = event.value, error = null) }
            is AddEditEvent.EnteredDescription -> _uiState.update { it.copy(description = event.value, error = null) }
            is AddEditEvent.EnteredDeadline -> _uiState.update { it.copy(deadlineText = event.value, error = null) }
            is AddEditEvent.SelectedPriority -> _uiState.update { it.copy(priority = event.value, error = null) }
            is AddEditEvent.SaveTask -> saveTask()
        }
    }

    private fun saveTask() {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.update { it.copy(error = "Judul tidak boleh kosong") }
            return
        }

        val deadline = state.deadlineText.toDeadlineInstantOrNull()
        if (state.deadlineText.isNotBlank() && deadline == null) {
            _uiState.update { it.copy(error = "Format deadline harus YYYY-MM-DD") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                if (currentTask != null) {
                    repository.updateTask(
                        currentTask!!.copy(
                            title = state.title.trim(),
                            description = state.description.trim(),
                            deadline = deadline,
                            priority = state.priority
                        )
                    )
                } else {
                    repository.insertTask(
                        Task(
                            title = state.title.trim(),
                            description = state.description.trim(),
                            deadline = deadline,
                            priority = state.priority
                        )
                    )
                }
                _uiState.update { it.copy(isSaved = true, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Gagal menyimpan tugas", isLoading = false) }
            }
        }
    }
}

sealed interface AddEditEvent {
    data class EnteredTitle(val value: String) : AddEditEvent
    data class EnteredDescription(val value: String) : AddEditEvent
    data class EnteredDeadline(val value: String) : AddEditEvent
    data class SelectedPriority(val value: TaskPriority) : AddEditEvent
    data object SaveTask : AddEditEvent
}

private fun Instant.toDateText(): String {
    return toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
}

private fun String.toDeadlineInstantOrNull(): Instant? {
    if (isBlank()) return null
    return runCatching {
        LocalDate.parse(trim()).atStartOfDayIn(TimeZone.currentSystemDefault())
    }.getOrNull()
}
