package com.studyhub.presentation.screens.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyhub.domain.model.Priority
import com.studyhub.domain.model.Subject
import com.studyhub.domain.model.Task
import com.studyhub.domain.model.TaskStatus
import com.studyhub.core.util.TaskColor
import com.studyhub.domain.usecase.subject.AddSubjectUseCase
import com.studyhub.domain.usecase.subject.GetAllSubjectsUseCase
import com.studyhub.domain.usecase.task.AddTaskUseCase
import com.studyhub.domain.usecase.task.GetTaskByIdUseCase
import com.studyhub.domain.usecase.task.UpdateTaskUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

sealed interface AddEditTaskUiState {
    object Idle : AddEditTaskUiState
    object Loading : AddEditTaskUiState
    data class Success(
        val existingTask: Task? = null,
        val subjects: List<Subject> = emptyList()
    ) : AddEditTaskUiState
    data class Error(val message: String) : AddEditTaskUiState
}

sealed interface AddEditTaskEvent {
    data class ShowSnackbar(val message: String) : AddEditTaskEvent
    object NavigateBack : AddEditTaskEvent
}

class AddEditTaskViewModel(
    private val addTaskUseCase: AddTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val getAllSubjectsUseCase: GetAllSubjectsUseCase,
    private val addSubjectUseCase: AddSubjectUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddEditTaskUiState>(AddEditTaskUiState.Idle)
    val uiState: StateFlow<AddEditTaskUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<AddEditTaskEvent>()
    val uiEvent: SharedFlow<AddEditTaskEvent> = _uiEvent.asSharedFlow()

    fun loadTask(taskId: String) {
        viewModelScope.launch {
            _uiState.update { AddEditTaskUiState.Loading }
            val subjects = getAllSubjectsUseCase()
            val task = getTaskByIdUseCase(taskId).firstOrNull()
            _uiState.update { AddEditTaskUiState.Success(existingTask = task, subjects = subjects) }
        }
    }

    fun loadSubjects() {
        viewModelScope.launch {
            val subjects = getAllSubjectsUseCase()
            val currentState = _uiState.value
            if (currentState is AddEditTaskUiState.Success) {
                _uiState.update { currentState.copy(subjects = subjects) }
            } else if (currentState is AddEditTaskUiState.Idle) {
                _uiState.update { AddEditTaskUiState.Success(subjects = subjects) }
            }
        }
    }

    fun resetState() {
        _uiState.update { AddEditTaskUiState.Idle }
    }

    fun addSubject(name: String) {
        viewModelScope.launch {
            try {
                val newSubject = Subject(
                    id = "sub_${Clock.System.now().toEpochMilliseconds()}",
                    name = name,
                    colorHex = "#8B7355", // Default color
                    icon = "school",
                    createdAt = Clock.System.now().toEpochMilliseconds()
                )
                addSubjectUseCase(newSubject)
                loadSubjects()
            } catch (e: Exception) {
                _uiState.update { AddEditTaskUiState.Error(e.message ?: "Gagal menambah mata kuliah") }
            }
        }
    }

    fun saveTask(
        taskId: String?,
        title: String,
        description: String,
        subject: String,
        priority: Priority,
        status: TaskStatus,
        dueDate: Long,
        dueTime: String?,
        estimatedMinutes: Int
    ) {
        viewModelScope.launch {
            val currentState = _uiState.value
            val existingTask = if (currentState is AddEditTaskUiState.Success) {
                currentState.existingTask
            } else null
            
            val existingCreatedAt = existingTask?.createdAt
            val existingColorHex = existingTask?.colorHex
            val existingCompletedAt = existingTask?.completedAt

            _uiState.update { AddEditTaskUiState.Loading }
            try {
                val now = Clock.System.now().toEpochMilliseconds()
                
                // Jika status DONE, gunakan completedAt yang lama jika ada, jika tidak ada gunakan now.
                // Jika status bukan DONE, completedAt adalah null.
                val completedAt = if (status == TaskStatus.DONE) {
                    existingCompletedAt ?: now
                } else null

                val task = Task(
                    id = taskId ?: "task_${now}",
                    title = title,
                    description = description,
                    subject = subject,
                    priority = priority,
                    status = status,
                    dueDate = dueDate,
                    dueTime = dueTime,
                    tags = emptyList(),
                    estimatedMinutes = estimatedMinutes,
                    isDeleted = false,
                    completedAt = completedAt,
                    createdAt = existingCreatedAt ?: now,
                    updatedAt = now,
                    colorHex = existingColorHex ?: TaskColor.random()
                )
                if (taskId == null) {
                    addTaskUseCase(task)
                } else {
                    updateTaskUseCase(task)
                }
                _uiState.update { AddEditTaskUiState.Idle }
                _uiEvent.emit(AddEditTaskEvent.ShowSnackbar(if (taskId == null) "Tugas berhasil ditambahkan" else "Tugas berhasil diperbarui"))
                _uiEvent.emit(AddEditTaskEvent.NavigateBack)
            } catch (e: Exception) {
                _uiState.update { AddEditTaskUiState.Error(e.message ?: "Gagal menyimpan tugas") }
            }
        }
    }
}
