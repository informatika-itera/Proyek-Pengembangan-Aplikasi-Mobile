package com.studyhub.presentation.screens.add_task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyhub.domain.model.Task
import com.studyhub.domain.repository.TaskRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AddTaskEvent {
    object TaskSaved : AddTaskEvent()
    data class Error(val message: String) : AddTaskEvent()
}

data class AddTaskUiState(
    val title: String = "",
    val description: String = "",
    val category: String = "Mobile App",
    val isLoading: Boolean = false
)

class AddTaskViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddTaskUiState())
    val state = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<AddTaskEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun loadTask(id: Long) {}

    fun onTitleChange(value: String) { _state.value = _state.value.copy(title = value) }
    fun onDescriptionChange(value: String) { _state.value = _state.value.copy(description = value) }
    fun onCategoryChange(value: String) { _state.value = _state.value.copy(category = value) }
    fun onStartTimeChange(value: String) {}
    fun onEndTimeChange(value: String) {}
    fun onColorChange(value: Long) {}
    fun onProgressChange(value: Int) {}

    fun saveTask() {
        viewModelScope.launch {
            _eventFlow.emit(AddTaskEvent.TaskSaved)
        }
    }
}
