package com.example.todomaster.presentation.screens.addtask

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todomaster.domain.model.Quadrant
import com.example.todomaster.domain.model.Task
import com.example.todomaster.domain.usecase.AddTaskUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class AddTaskViewModel(
    private val addTaskUseCase: AddTaskUseCase
) : ViewModel() {

    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var priority by mutableStateOf(Quadrant.SCHEDULE)
    var error by mutableStateOf<String?>(null)

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onSaveTask() {
        if (title.isBlank()) {
            error = "Judul tidak boleh kosong"
            return
        }

        viewModelScope.launch {
            val newTask = Task(
                id = 0,
                title = title,
                description = description,
                priority = priority,
                dueDate = 0,
                isCompleted = false,
                isPinned = false,
                subTasks = emptyList(),
                createdAt = Clock.System.now().toEpochMilliseconds()
            )

            addTaskUseCase(newTask).onSuccess {
                _uiEvent.emit(UiEvent.SaveSuccess)
            }.onFailure { e ->
                error = e.message
            }
        }
    }

    sealed class UiEvent {
        data object SaveSuccess : UiEvent()
    }
}