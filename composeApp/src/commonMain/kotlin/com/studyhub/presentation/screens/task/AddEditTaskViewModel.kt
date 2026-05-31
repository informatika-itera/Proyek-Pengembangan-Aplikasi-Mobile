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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

data class AddEditTaskUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val existingTask: Task? = null,
    val subjects: List<Subject> = emptyList(),
    val error: String? = null
)

class AddEditTaskViewModel(
    private val addTaskUseCase: AddTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val getAllSubjectsUseCase: GetAllSubjectsUseCase,
    private val addSubjectUseCase: AddSubjectUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditTaskUiState())
    val uiState: StateFlow<AddEditTaskUiState> = _uiState.asStateFlow()

    fun loadTask(taskId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val subjects = getAllSubjectsUseCase()
            val task = getTaskByIdUseCase(taskId).firstOrNull()
            _uiState.update { it.copy(isLoading = false, existingTask = task, subjects = subjects) }
        }
    }

    fun loadSubjects() {
        viewModelScope.launch {
            val subjects = getAllSubjectsUseCase()
            _uiState.update { it.copy(subjects = subjects) }
        }
    }

    fun resetState() {
        _uiState.update { AddEditTaskUiState(isSuccess = false) }
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
                _uiState.update { it.copy(error = e.message) }
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
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val now = Clock.System.now().toEpochMilliseconds()
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
                    createdAt = _uiState.value.existingTask?.createdAt ?: now,
                    updatedAt = now
                )
                if (taskId == null) addTaskUseCase(task)
                else updateTaskUseCase(task)
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
