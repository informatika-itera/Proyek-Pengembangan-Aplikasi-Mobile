package com.example.hujjah.presentation.screens.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hujjah.domain.model.Note
import com.example.hujjah.domain.repository.NoteRepository
import com.example.hujjah.domain.usecase.DeleteNoteUseCase
import com.example.hujjah.domain.usecase.GetAllNotesUseCase
import com.example.hujjah.domain.usecase.SearchNotesUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi

data class NotesUiState(
    val notes: List<Note> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val categories: List<String> = emptyList()
)

sealed interface NotesEvent {
    val message: String? get() = null // default fallback
    data class Error(override val message: String) : NotesEvent
    data object NotePinnedToggled : NotesEvent
    data object NoteDeleted : NotesEvent
}

@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModel(
    private val repository: NoteRepository,
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val searchNotesUseCase: SearchNotesUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<NotesEvent>()
    val events: SharedFlow<NotesEvent> = _events.asSharedFlow()

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow<String?>(null)

    init {
        loadNotes()
    }

    private fun loadNotes() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            launch {
                getAllNotesUseCase().collect { allNotes ->
                    val distinctCategories = allNotes
                        .map { it.category }
                        .filter { it.isNotBlank() }
                        .distinct()
                    _uiState.update { it.copy(categories = distinctCategories) }
                }
            }

            combine(
                _searchQuery.debounce(200).distinctUntilChanged(),
                _selectedCategory
            ) { query, category ->
                query to category
            }.flatMapLatest { (query, category) ->
                searchNotesUseCase(query, category).map { notesList ->
                    Triple(notesList, query, category)
                }
            }.collect { (notesList, query, category) ->
                _uiState.update {
                    it.copy(
                        notes = notesList,
                        searchQuery = query,
                        selectedCategory = category,
                        isLoading = false,
                        error = null
                    )
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onCategorySelected(category: String?) {
        _selectedCategory.value = category
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun togglePin(noteId: Long) {
        viewModelScope.launch {
            try {
                repository.togglePinNote(noteId)
                _events.emit(NotesEvent.NotePinnedToggled)
            } catch (e: Exception) {
                _events.emit(NotesEvent.Error("Gagal menyematkan catatan"))
            }
        }
    }

    fun deleteNote(noteId: Long) {
        viewModelScope.launch {
            deleteNoteUseCase(noteId)
                .onSuccess {
                    _events.emit(NotesEvent.NoteDeleted)
                }
                .onFailure { error ->
                    _events.emit(NotesEvent.Error(error.message ?: "Gagal menghapus catatan"))
                }
        }
    }
}
