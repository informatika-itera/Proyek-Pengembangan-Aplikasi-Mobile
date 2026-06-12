package com.studymate.presentation.screens.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studymate.domain.model.Note
import com.studymate.domain.repository.NoteRepository
import com.studymate.domain.repository.ActivityRepository
import com.studymate.domain.usecase.RefineNoteUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

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

    init {
        observeNotes()
    }

    private fun observeNotes() {
        viewModelScope.launch {
            noteRepository.getAllNotes()
                .combine(_searchQuery) { notes, query ->
                    if (notes.isEmpty()) {
                        NotesUiState.Empty
                    } else {
                        val filtered = if (query.isBlank()) {
                            notes
                        } else {
                            notes.filter { note ->
                                note.title.contains(query, ignoreCase = true) ||
                                        note.rawContent.contains(query, ignoreCase = true) ||
                                        note.subject.contains(query, ignoreCase = true) ||
                                        note.refinedContent?.contains(query, ignoreCase = true) == true
                            }
                        }
                        if (filtered.isEmpty()) NotesUiState.Empty else NotesUiState.Success(filtered)
                    }
                }
                .catch { e ->
                    _uiState.emit(NotesUiState.Error(e.message ?: "Gagal memuat catatan"))
                }
                .collect { state ->
                    _uiState.emit(state)
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun addNote(title: String, rawContent: String, subject: String, onComplete: () -> Unit = {}) {
        if (title.isBlank() || subject.isBlank()) {
            viewModelScope.launch {
                _events.emit(NoteEvent.ShowMessage("Judul dan Mata Pelajaran tidak boleh kosong"))
            }
            return
        }
        viewModelScope.launch {
            try {
                val newNote = Note(
                    title = title,
                    rawContent = rawContent,
                    subject = subject
                )
                noteRepository.insertNote(newNote)
                // Record activity so streaks are updated when user creates a note
                try {
                    activityRepository.recordNoteCreation()
                } catch (_: Exception) {
                    // non-fatal: don't block UX if activity recording fails
                }
                _events.emit(NoteEvent.ShowMessage("Catatan berhasil disimpan!"))
                onComplete()
            } catch (e: Exception) {
                _events.emit(NoteEvent.ShowMessage("Gagal menyimpan: ${e.message}"))
            }
        }
    }

    fun updateNote(note: Note, onComplete: () -> Unit = {}) {
        if (note.title.isBlank() || note.subject.isBlank()) {
            viewModelScope.launch {
                _events.emit(NoteEvent.ShowMessage("Judul dan Mata Pelajaran tidak boleh kosong"))
            }
            return
        }
        viewModelScope.launch {
            try {
                val updatedNote = note.copy(
                    updatedAt = Clock.System.now().toEpochMilliseconds()
                )
                noteRepository.updateNote(updatedNote)
                _events.emit(NoteEvent.ShowMessage("Catatan diperbarui!"))
                onComplete()
            } catch (e: Exception) {
                _events.emit(NoteEvent.ShowMessage("Gagal memperbarui: ${e.message}"))
            }
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

    fun refineContent(subject: String, title: String, content: String, onRefined: (String) -> Unit) {
        viewModelScope.launch {
            val currentState = _uiState.value
            val notes = if (currentState is NotesUiState.Success) currentState.notes else emptyList()
            
            _uiState.emit(NotesUiState.Refining(notes, -1L))
            
            val result = refineNoteUseCase.refineRawContent(subject, title, content)
            
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
