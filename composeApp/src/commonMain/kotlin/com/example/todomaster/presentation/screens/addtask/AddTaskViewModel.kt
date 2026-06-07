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

data class SelectableSubTask(
    val response: SubTaskResponse,
    val isSelected: Boolean = true
)

class AddTaskViewModel(
    private val addTaskUseCase: AddTaskUseCase,
    private val repository: TaskRepository,
    private val geminiService: GeminiService
) : ViewModel() {

    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var priority by mutableStateOf(Quadrant.SCHEDULE)
    var error by mutableStateOf<String?>(null)

    var dueDate by mutableStateOf<Long?>(null)
    var isLoadingAi by mutableStateOf(false)
    var generatedSubTasks by mutableStateOf<List<SelectableSubTask>>(emptyList())
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
                dueDate = it.dueDate
            }
        }
    }

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
                    val jsonParser = Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    }

                    val parsedList = jsonParser.decodeFromString<List<SubTaskResponse>>(jsonResult)

                    if (parsedList.isEmpty()) {
                        error = "Tugas ini tergolong sederhana. Kamu pasti bisa mengeksekusinya langsung tanpa bantuan AI!"
                        generatedSubTasks = emptyList()
                    } else {
                        generatedSubTasks = parsedList.map { SelectableSubTask(it) }
                    }

                } catch (e: Exception) {
                    println("RESPONS MURNI GEMINI: $jsonResult")
                    error = "Gagal parsing: ${e.message}"
                }
            }.onFailure { e ->
                error = "Koneksi gagal: ${e.message}"
            }
            isLoadingAi = false
        }
    }

    fun toggleSubTaskSelection(index: Int, isChecked: Boolean) {
        val newList = generatedSubTasks.toMutableList()
        if (index in newList.indices) {
            newList[index] = newList[index].copy(isSelected = isChecked)
            generatedSubTasks = newList
        }
    }

    fun acceptAiSubTasks() {
        if (generatedSubTasks.isNotEmpty()) {
            val builder = StringBuilder()
            builder.append(description)
            if (description.isNotBlank()) builder.append("\n\n")
            builder.append("📋 Rekomendasi Sub-Task (AI):\n")
            generatedSubTasks.forEachIndexed { index, sub ->
                builder.append("${index + 1}. ${sub.response.title} (${sub.response.estimatedMinutes} mnt)\n")
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
            val parentTask = Task(
                id = editingTaskId ?: 0,
                title = title,
                description = description,
                priority = priority,
                dueDate = dueDate,
                isCompleted = false,
                isPinned = false,
                subTasks = emptyList(),
                createdAt = Clock.System.now().toEpochMilliseconds(),
                isAiGenerated = false,
                parentTaskTitle = null
            )

            if (editingTaskId != null) {
                repository.updateTask(parentTask)
            } else {
                addTaskUseCase(parentTask).onFailure { e ->
                    error = e.message
                    return@launch
                }
            }

            val selectedAiTasks = generatedSubTasks.filter { it.isSelected }

            selectedAiTasks.forEach { selectable ->
                val sub = selectable.response

                val aiQuadrant = try {
                    Quadrant.valueOf(sub.recommended_quadrant)
                } catch (e: Exception) {
                    Quadrant.SCHEDULE
                }

                val childTask = Task(
                    id = 0,
                    title = sub.title,
                    description = "Estimasi: ${sub.estimatedMinutes} menit\n(Bagian dari: $title)",
                    priority = aiQuadrant,
                    dueDate = 0,
                    isCompleted = false,
                    isPinned = false,
                    subTasks = emptyList(),
                    createdAt = Clock.System.now().toEpochMilliseconds(),
                    isAiGenerated = true,
                    parentTaskTitle = title
                )

                addTaskUseCase(childTask)
            }

            _uiEvent.emit(UiEvent.SaveSuccess)
        }
    }

    sealed class UiEvent {
        data object SaveSuccess : UiEvent()
    }
}