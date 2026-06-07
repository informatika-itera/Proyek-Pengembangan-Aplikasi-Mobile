package com.studyhub.presentation.screens.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyhub.domain.model.Priority
import com.studyhub.domain.model.Subject
import com.studyhub.domain.model.Task
import com.studyhub.domain.model.TaskStatus
import com.studyhub.domain.usecase.subject.AddSubjectUseCase
import com.studyhub.domain.usecase.subject.GetAllSubjectsUseCase
import com.studyhub.domain.usecase.task.AddTaskUseCase
import com.studyhub.domain.usecase.task.GetTaskByIdUseCase
import com.studyhub.domain.usecase.task.UpdateTaskUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

sealed interface AddEditTaskUiState {
    object Loading : AddEditTaskUiState
    data class Success(
        val existingTask: Task? = null,
        val subjects: List<Subject> = emptyList()
    ) : AddEditTaskUiState
    data class Error(val message: String) : AddEditTaskUiState
}

sealed interface AddEditTaskUiEvent {
    object SaveSuccess : AddEditTaskUiEvent
    data class ShowSnackbar(val message: String) : AddEditTaskUiEvent
}

class AddEditTaskViewModel(
    private val addTaskUseCase: AddTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val getAllSubjectsUseCase: GetAllSubjectsUseCase,
    private val addSubjectUseCase: AddSubjectUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddEditTaskUiState>(AddEditTaskUiState.Loading)
    val uiState: StateFlow<AddEditTaskUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<AddEditTaskUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadTask(taskId: String?) {
        viewModelScope.launch {
            _uiState.value = AddEditTaskUiState.Loading
            try {
                val subjects = getAllSubjectsUseCase()
                val task = if (taskId != null) getTaskByIdUseCase(taskId).firstOrNull() else null
                _uiState.value = AddEditTaskUiState.Success(existingTask = task, subjects = subjects)
            } catch (e: Exception) {
                _uiState.value = AddEditTaskUiState.Error(e.message ?: "Gagal memuat data")
            }
        }
    }

    fun loadSubjectsOnly() {
        if (_uiState.value is AddEditTaskUiState.Success) {
            viewModelScope.launch {
                val subjects = getAllSubjectsUseCase()
                val current = (_uiState.value as AddEditTaskUiState.Success)
                _uiState.value = current.copy(subjects = subjects)
            }
        } else {
            loadTask(null)
        }
    }

    fun addSubject(name: String) {
        viewModelScope.launch {
            try {
                val newSubject = Subject(
                    id = "sub_${Clock.System.now().toEpochMilliseconds()}",
                    name = name,
                    colorHex = "#8B7355",
                    icon = "school",
                    createdAt = Clock.System.now().toEpochMilliseconds()
                )
                addSubjectUseCase(newSubject)
                loadSubjectsOnly()
                _uiEvent.emit(AddEditTaskUiEvent.ShowSnackbar("Mata kuliah berhasil ditambahkan"))
            } catch (e: Exception) {
                _uiEvent.emit(AddEditTaskUiEvent.ShowSnackbar(e.message ?: "Gagal menambah mata kuliah"))
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
        estimatedMinutes: Int
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val now = Clock.System.now().toEpochMilliseconds()
                val existing = if (_uiState.value is AddEditTaskUiState.Success) {
                    (_uiState.value as AddEditTaskUiState.Success).existingTask
                } else null

                val task = Task(
                    id = taskId ?: "task_${now}",
                    title = title,
                    description = description,
                    subject = subject,
                    priority = priority,
                    status = status,
                    dueDate = dueDate,
                    dueTime = null,
                    tags = emptyList(),
                    estimatedMinutes = estimatedMinutes,
                    isDeleted = false,
                    completedAt = if (status == TaskStatus.DONE) now else null,
                    createdAt = existing?.createdAt ?: now,
                    updatedAt = now
                )
                if (taskId == null) addTaskUseCase(task)
                else updateTaskUseCase(task)
                
                _uiEvent.emit(AddEditTaskUiEvent.SaveSuccess)
            } catch (e: Exception) {
                _uiEvent.emit(AddEditTaskUiEvent.ShowSnackbar(e.message ?: "Gagal menyimpan tugas"))
            } finally {
                _isLoading.value = false
            }
        }
    }
}
