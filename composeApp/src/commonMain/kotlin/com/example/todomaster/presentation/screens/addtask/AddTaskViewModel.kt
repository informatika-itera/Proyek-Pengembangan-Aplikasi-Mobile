package com.example.todomaster.presentation.screens.addtask

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todomaster.domain.model.Quadrant
import com.example.todomaster.domain.model.Task
import com.example.todomaster.domain.repository.TaskRepository
import com.example.todomaster.domain.usecase.AddTaskUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class AddTaskViewModel(
    private val addTaskUseCase: AddTaskUseCase,
    private val repository: TaskRepository
) : ViewModel() {

    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var priority by mutableStateOf(Quadrant.SCHEDULE)
    var error by mutableStateOf<String?>(null)

    private var editingTaskId by mutableStateOf<Long?>(null)
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun loadTaskForEdit(taskId: Long) {
        viewModelScope.launch {
            editingTaskId = taskId
            val task = repository.getTaskById(taskId)
            task?.let {
                title = it.title
                description = it.description ?: ""
                priority = it.priority
            }
        }
    }

    fun onSaveTask() {
        if (title.isBlank()) {
            error = "Judul tidak boleh kosong"
            return
        }

        viewModelScope.launch {
            val newTask = Task(
                id = editingTaskId ?: 0,
                title = title,
                description = description,
                priority = priority,
                dueDate = 0,
                isCompleted = false,
                isPinned = false,
                subTasks = emptyList(),
                createdAt = Clock.System.now().toEpochMilliseconds()
            )

            if (editingTaskId != null) {
                repository.updateTask(newTask)
                _uiEvent.emit(UiEvent.SaveSuccess)
            } else {
                addTaskUseCase(newTask).onSuccess {
                    _uiEvent.emit(UiEvent.SaveSuccess)
                }.onFailure { e ->
                    error = e.message
                }
            }
        }
    }

    sealed class UiEvent {
        data object SaveSuccess : UiEvent()
    }
}