package com.example.Feelia.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Feelia.domain.model.Emotion
import com.example.Feelia.domain.model.Note
import com.example.Feelia.domain.repository.NoteRepository
import com.example.Feelia.domain.usecase.DeleteNoteUseCase
import com.example.Feelia.domain.usecase.GetAllNotesUseCase
import com.example.Feelia.domain.usecase.NoteSortBy
import com.example.Feelia.domain.usecase.SearchNotesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val searchNotesUseCase: SearchNotesUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val repository: NoteRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    // DIPERBAIKI: NoteCategory → Emotion, sesuai domain model Feelia
    private val _selectedEmotion = MutableStateFlow<Emotion?>(null)
    private val _sortBy = MutableStateFlow(NoteSortBy.UPDATED_DESC)
    private val _isLoading = MutableStateFlow(false)

    private val debouncedSearchQuery = _searchQuery.debounce(300)

    val sortBy: StateFlow<NoteSortBy> = _sortBy

    val uiState: StateFlow<HomeUiState> = combine(
        debouncedSearchQuery,
        _selectedEmotion,
        _sortBy
    ) { query, emotion, sortBy ->
        Triple(query, emotion, sortBy)
    }.flatMapLatest { (query, emotion, sortBy) ->
        if (query.isBlank() && emotion == null) {
            getAllNotesUseCase(sortBy)
        } else {
            searchNotesUseCase(query, emotion)
        }
    }.combine(_isLoading) { notes, isLoading ->
        when {
            isLoading -> HomeUiState.Loading
            notes.isEmpty() -> HomeUiState.Empty(
                query = _searchQuery.value,
                emotion = _selectedEmotion.value
            )
            else -> HomeUiState.Success(
                notes = notes,
                query = _searchQuery.value,
                emotion = _selectedEmotion.value,
                sortBy = _sortBy.value
            )
        }
    }.catch { e ->
        emit(HomeUiState.Error(e.message ?: "Terjadi kesalahan"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState.Loading
    )

    // ==================== USER ACTIONS ====================

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    // DIPERBAIKI: onCategorySelected → onEmotionSelected
    fun onEmotionSelected(emotion: Emotion?) {
        _selectedEmotion.value = emotion
    }

    fun onSortByChanged(sortBy: NoteSortBy) {
        _sortBy.value = sortBy
    }

    fun togglePin(noteId: Long) {
        viewModelScope.launch {
            repository.togglePinNote(noteId)
        }
    }

    fun deleteNote(noteId: Long) {
        viewModelScope.launch {
            deleteNoteUseCase(noteId)
        }
    }
    // DIHAPUS: deleteNotes(noteIds) — tidak ada di NoteRepository interface
}

// =====================================================================
// HomeUiState
// DIPERBAIKI: field 'category: NoteCategory?' → 'emotion: Emotion?'
// =====================================================================
sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Success(
        val notes: List<Note>,
        val query: String = "",
        val emotion: Emotion? = null,
        val sortBy: NoteSortBy = NoteSortBy.UPDATED_DESC
    ) : HomeUiState

    data class Empty(
        val query: String = "",
        val emotion: Emotion? = null
    ) : HomeUiState

    data class Error(val message: String) : HomeUiState
}