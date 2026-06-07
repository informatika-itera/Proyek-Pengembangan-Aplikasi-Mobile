package com.studymate.presentation.screens.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studymate.domain.model.Note
import com.studymate.domain.repository.NoteRepository
import com.studymate.domain.repository.ActivityRepository
import com.studymate.domain.usecase.RefineNoteUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

sealed class NoteEvent {
    data class ShowMessage(val message: String) : NoteEvent()
    data class NavigateTo(val route: String) : NoteEvent()
}

class NotesViewModel(
    private val noteRepository: NoteRepository,
    private val refineNoteUseCase: RefineNoteUseCase,
    private val activityRepository: ActivityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<NotesUiState>(NotesUiState.Loading)
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _events = MutableSharedFlow<NoteEvent>()
    val events: SharedFlow<NoteEvent> = _events.asSharedFlow()

    private var allNotes: List<Note> = emptyList()

    init {
        loadNotes()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            noteRepository.getAllNotes()
                .catch { e ->
                    _uiState.emit(NotesUiState.Error(e.message ?: "Gagal memuat catatan"))
                }
                .collect { notes ->
                    allNotes = notes
                    filterNotes(_searchQuery.value)
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        filterNotes(query)
    }

    private fun filterNotes(query: String) {
        viewModelScope.launch {
            if (allNotes.isEmpty()) {
                _uiState.emit(NotesUiState.Empty)
                return@launch
            }

            val filtered = if (query.isBlank()) {
                allNotes
            } else {
                allNotes.filter {
                    it.title.contains(query, ignoreCase = true) ||
                            it.rawContent.contains(query, ignoreCase = true) ||
                            it.subject.contains(query, ignoreCase = true) ||
                            it.refinedContent?.contains(query, ignoreCase = true) == true
                }
            }

            if (filtered.isEmpty() && query.isNotBlank()) {
                _uiState.emit(NotesUiState.Empty) // Or a specific NoResults state
            } else if (filtered.isEmpty()) {
                _uiState.emit(NotesUiState.Empty)
            } else {
                _uiState.emit(NotesUiState.Success(filtered))
            }
        }
    }

    fun addNote(title: String, rawContent: String, subject: String) {
        if (title.isBlank() || rawContent.isBlank()) return
        viewModelScope.launch {
            noteRepository.insertNote(Note(title = title, rawContent = rawContent, subject = subject))
            activityRepository.recordNoteCreation()
            _events.emit(NoteEvent.ShowMessage("Catatan berhasil disimpan!"))
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            noteRepository.updateNote(note)
            _events.emit(NoteEvent.ShowMessage("Catatan diperbarui!"))
        }
    }

    fun refineNote(note: Note) {
        val currentState = _uiState.value
        if (currentState is NotesUiState.Success) {
            viewModelScope.launch {
                _uiState.emit(NotesUiState.Refining(currentState.notes, note.id))
                val result = refineNoteUseCase(note)
                if (result.isSuccess) {
                    _events.emit(NoteEvent.ShowMessage("Catatan berhasil dirapikan AI ✨"))
                } else {
                    _uiState.emit(NotesUiState.Success(currentState.notes))
                    _events.emit(NoteEvent.ShowMessage("Gagal: ${result.exceptionOrNull()?.message}"))
                }
            }
        }
    }

    fun refineContent(content: String, onRefined: (String) -> Unit) {
        viewModelScope.launch {
            val currentState = _uiState.value
            val notes = if (currentState is NotesUiState.Success) currentState.notes else emptyList()
            
            _uiState.emit(NotesUiState.Refining(notes, -1L))
            
            val result = refineNoteUseCase.refineRawContent(content)
            
            if (result.isSuccess) {
                val refinedText = result.getOrThrow()
                onRefined(refinedText)
                _uiState.emit(if (notes.isEmpty()) NotesUiState.Empty else NotesUiState.Success(notes))
                _events.emit(NoteEvent.ShowMessage("Konten berhasil dijelaskan AI ✨"))
            } else {
                _uiState.emit(if (notes.isEmpty()) NotesUiState.Empty else NotesUiState.Success(notes))
                _events.emit(NoteEvent.ShowMessage("Gagal: ${result.exceptionOrNull()?.message}"))
            }
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            noteRepository.deleteNote(id)
            _events.emit(NoteEvent.ShowMessage("Catatan dihapus"))
        }
    }
}
