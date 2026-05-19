package com.example.noteai.presentation.screens.addnote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteai.domain.model.Note
import com.example.noteai.domain.model.NoteCategory
import com.example.noteai.domain.model.NoteColor
import com.example.noteai.domain.repository.NoteRepository
import com.example.noteai.domain.usecase.SaveNoteUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class AddNoteViewModel(
    private val repository: NoteRepository,
    private val saveNoteUseCase: SaveNoteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddNoteUiState())
    val uiState: StateFlow<AddNoteUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AddNoteEvent>()
    val events: SharedFlow<AddNoteEvent> = _events.asSharedFlow()

    private var currentNoteId: Long? = null

    fun loadNote(noteId: Long) {
        currentNoteId = noteId
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            repository.getNoteById(noteId).collect { note ->
                note?.let {
                    val parsedContent = parseMealContent(note.content)

                    _uiState.update { state ->
                        state.copy(
                            title = note.title,
                            price = parsedContent.price,
                            content = parsedContent.note,
                            category = note.category,
                            color = note.color,
                            isLoading = false,
                            isEditMode = true,
                            createdAt = note.createdAt
                        )
                    }
                }
            }
        }
    }

    fun onTitleChange(title: String) {
        _uiState.update { it.copy(title = title, titleError = null) }
    }

    fun onPriceChange(price: String) {
        val cleanPrice = price.filter { it.isDigit() }
        _uiState.update { it.copy(price = cleanPrice) }
    }

    fun onContentChange(content: String) {
        _uiState.update { it.copy(content = content) }
    }

    fun onCategoryChange(category: NoteCategory) {
        _uiState.update { it.copy(category = category) }
    }

    fun onColorChange(color: NoteColor) {
        _uiState.update { it.copy(color = color) }
    }

    fun saveNote() {
        val state = _uiState.value

        if (state.title.isBlank()) {
            _uiState.update { it.copy(titleError = "Nama makanan wajib diisi") }
            return
        }

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            val note = Note(
                id = currentNoteId ?: 0,
                title = state.title.trim(),
                content = buildMealContent(state.price, state.content),
                category = state.category,
                color = state.color,
                createdAt = if (currentNoteId == null) Clock.System.now() else state.createdAt,
                updatedAt = Clock.System.now()
            )

            saveNoteUseCase(note)
                .onSuccess {
                    _events.emit(AddNoteEvent.NoteSaved)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isSaving = false) }
                    _events.emit(AddNoteEvent.Error(error.message ?: "Gagal menyimpan"))
                }
        }
    }

    fun applyAISuggestion(newContent: String) {
        _uiState.update { it.copy(content = newContent) }
    }

    fun applyAITitle(newTitle: String) {
        _uiState.update { it.copy(title = newTitle) }
    }

    private fun buildMealContent(price: String, note: String): String {
        return buildString {
            if (price.isNotBlank()) {
                appendLine("Harga: Rp$price")
            }
            if (note.isNotBlank()) {
                append(note.trim())
            }
        }.trim()
    }

    private fun parseMealContent(content: String): ParsedMealContent {
        val lines = content.lines()
        val firstLine = lines.firstOrNull().orEmpty()
        val hasPrice = firstLine.startsWith("Harga: Rp")

        return if (hasPrice) {
            ParsedMealContent(
                price = firstLine.removePrefix("Harga: Rp").filter { it.isDigit() },
                note = lines.drop(1).joinToString("\n").trim()
            )
        } else {
            ParsedMealContent(price = "", note = content)
        }
    }

    private data class ParsedMealContent(val price: String, val note: String)
}

data class AddNoteUiState(
    val title: String = "",
    val price: String = "",
    val content: String = "",
    val category: NoteCategory = NoteCategory.LUNCH,
    val color: NoteColor = NoteColor.DEFAULT,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isEditMode: Boolean = false,
    val titleError: String? = null,
    val createdAt: Instant = Clock.System.now()
) {
    val isValid: Boolean get() = title.isNotBlank()
    val canSave: Boolean get() = isValid && !isSaving
}

sealed interface AddNoteEvent {
    data object NoteSaved : AddNoteEvent
    data class Error(val message: String) : AddNoteEvent
}
