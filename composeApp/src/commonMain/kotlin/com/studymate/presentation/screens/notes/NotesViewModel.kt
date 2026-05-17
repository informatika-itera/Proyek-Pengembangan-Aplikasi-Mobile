package com.studymate.presentation.screens.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studymate.domain.model.Note
import com.studymate.domain.repository.NoteRepository
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
    private val refineNoteUseCase: RefineNoteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<NotesUiState>(NotesUiState.Loading)
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<NoteEvent>()
    val events: SharedFlow<NoteEvent> = _events.asSharedFlow()

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
                    if (notes.isEmpty()) {
                        _uiState.emit(NotesUiState.Empty)
                    } else {
                        _uiState.emit(NotesUiState.Success(notes))
                    }
                }
        }
    }

    fun addNote(title: String, rawContent: String, subject: String) {
        if (title.isBlank() || rawContent.isBlank()) return
        viewModelScope.launch {
            noteRepository.insertNote(Note(title = title, rawContent = rawContent, subject = subject))
            _events.emit(NoteEvent.ShowMessage("Catatan berhasil disimpan!"))
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

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            noteRepository.deleteNote(id)
            _events.emit(NoteEvent.ShowMessage("Catatan dihapus"))
        }
    }
}
