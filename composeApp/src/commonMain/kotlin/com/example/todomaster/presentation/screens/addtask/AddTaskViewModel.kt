package com.example.todomaster.presentation.screens.addtask

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todomaster.data.remote.api.GeminiService
import com.example.todomaster.data.remote.api.SystemPrompts
import com.example.todomaster.data.remote.dto.SubTaskResponse
import com.example.todomaster.domain.model.Quadrant
import com.example.todomaster.domain.model.Task
import com.example.todomaster.domain.repository.TaskRepository
import com.example.todomaster.domain.usecase.AddTaskUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json

class AddTaskViewModel(
    private val addTaskUseCase: AddTaskUseCase,
    private val repository: TaskRepository,
    private val geminiService: GeminiService
) : ViewModel() {

    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var priority by mutableStateOf(Quadrant.SCHEDULE)
    var error by mutableStateOf<String?>(null)

    // VARIABEL AI YANG ERROR TADI ADA DI SINI
    var isLoadingAi by mutableStateOf(false)
    var generatedSubTasks by mutableStateOf<List<SubTaskResponse>>(emptyList())
    var showAiDialog by mutableStateOf(false)

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

    // FUNGSI AI YANG ERROR TADI ADA DI SINI
    fun breakdownTaskWithAI() {
        if (title.isBlank()) {
            error = "Isi judul tugas terlebih dahulu sebelum meminta bantuan AI!"
            return
        }

        viewModelScope.launch {
            isLoadingAi = true
            error = null

            geminiService.generateContent(
                prompt = "Tolong uraikan tugas kuliah berikut: $title",
                systemPrompt = SystemPrompts.TASK_BREAKDOWN_ASSISTANT
            ).onSuccess { jsonResult ->
                try {
                    val parsedList = Json.decodeFromString<List<SubTaskResponse>>(jsonResult)
                    generatedSubTasks = parsedList
                    showAiDialog = true
                } catch (e: Exception) {
                    error = "Gagal membaca format data AI. Coba klik lagi."
                }
            }.onFailure { e ->
                error = "Koneksi gagal: ${e.message}"
            }
            isLoadingAi = false
        }
    }

    fun acceptAiSubTasks() {
        if (generatedSubTasks.isNotEmpty()) {
            val builder = StringBuilder()
            builder.append(description)
            if (description.isNotBlank()) builder.append("\n\n")
            builder.append("📋 Rekomendasi Sub-Task (AI):\n")
            generatedSubTasks.forEachIndexed { index, sub ->
                builder.append("${index + 1}. ${sub.title} (${sub.estimatedMinutes} mnt)\n")
            }
            description = builder.toString()
        }
        showAiDialog = false
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