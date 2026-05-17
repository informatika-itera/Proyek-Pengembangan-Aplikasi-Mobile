package com.studyhub.presentation.screens.add_task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyhub.domain.model.Task
import com.studyhub.domain.repository.TaskRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed class AddTaskEvent {
    object TaskSaved : AddTaskEvent()
    data class Error(val message: String) : AddTaskEvent()
}

data class AddTaskUiState(
    val title: String = "",
    val description: String = "",
    val category: String = "Mobile App",
    val priority: String = "High",
    val dueDate: String = "Today",
    val startTime: String = "09:00 AM",
    val endTime: String = "11:00 AM",
    val colorHex: Long = 0xFFFFEADA, // PastelOrange default
    val progress: Int = 0,
    val isLoading: Boolean = false
)

class AddTaskViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddTaskUiState())
    val state = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<AddTaskEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var currentTaskId: Long? = null

    fun loadTask(id: Long) {
        if (id == -1L) return
        currentTaskId = id
        viewModelScope.launch {
            repository.getTaskById(id).collectLatest { task ->
                task?.let {
                    _state.value = _state.value.copy(
                        title = it.title,
                        description = it.description,
                        category = it.category,
                        priority = it.priority,
                        dueDate = it.dueDate,
                        startTime = it.startTime,
                        endTime = it.endTime,
                        colorHex = it.colorHex,
                        progress = it.progress
                    )
                }
            }
        }
    }

    fun onTitleChange(value: String) { _state.value = _state.value.copy(title = value) }
    fun onDescriptionChange(value: String) { _state.value = _state.value.copy(description = value) }
    fun onCategoryChange(value: String) { _state.value = _state.value.copy(category = value) }
    fun onStartTimeChange(value: String) { _state.value = _state.value.copy(startTime = value) }
    fun onEndTimeChange(value: String) { _state.value = _state.value.copy(endTime = value) }
    fun onColorChange(value: Long) { _state.value = _state.value.copy(colorHex = value) }
    fun onProgressChange(value: Int) { _state.value = _state.value.copy(progress = value) }

    fun saveTask() {
        viewModelScope.launch {
            val s = _state.value
            try {
                if (s.title.isBlank()) {
                    _eventFlow.emit(AddTaskEvent.Error("Title cannot be empty"))
                    return@launch
                }
                val task = Task(
                    id = currentTaskId,
                    title = s.title,
                    description = s.description,
                    category = s.category,
                    priority = s.priority,
                    dueDate = s.dueDate,
                    startTime = s.startTime,
                    endTime = s.endTime,
                    colorHex = s.colorHex,
                    progress = s.progress
                )
                
                if (currentTaskId == null) {
                    repository.insertTask(task)
                } else {
                    repository.updateTask(task)
                }
                _eventFlow.emit(AddTaskEvent.TaskSaved)
            } catch (e: Exception) {
                _eventFlow.emit(AddTaskEvent.Error(e.message ?: "Failed to save task"))
            }
        }
    }
}
