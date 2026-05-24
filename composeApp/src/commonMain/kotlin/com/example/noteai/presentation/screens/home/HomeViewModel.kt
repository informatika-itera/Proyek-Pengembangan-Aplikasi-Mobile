package com.example.noteai.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteai.domain.model.Note
import com.example.noteai.domain.model.VulnSeverity
import com.example.noteai.domain.repository.NoteRepository
import com.example.noteai.domain.usecase.DeleteNoteUseCase
import com.example.noteai.domain.usecase.GetAllNotesUseCase
import com.example.noteai.domain.usecase.NoteSortBy
import com.example.noteai.domain.usecase.SearchNotesUseCase
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
    val searchQuery: StateFlow<String> = _searchQuery
    
    private val _selectedSeverity = MutableStateFlow<VulnSeverity?>(null)
    private val _sortBy = MutableStateFlow(NoteSortBy.UPDATED_DESC)
    private val _isLoading = MutableStateFlow(false)
    
    private val debouncedSearchQuery = _searchQuery.debounce(300)
    
    val sortBy: StateFlow<NoteSortBy> = _sortBy
    
    val uiState: StateFlow<HomeUiState> = combine(
        debouncedSearchQuery,
        _selectedSeverity,
        _sortBy
    ) { query, severity, sortBy ->
        Triple(query, severity, sortBy)
    }.flatMapLatest { (query, severity, sortBy) ->
        if (query.isBlank() && severity == null) {
            getAllNotesUseCase(sortBy)
        } else {
            searchNotesUseCase(query, severity)
        }
    }.combine(_isLoading) { notes, isLoading ->
        when {
            isLoading -> HomeUiState.Loading
            notes.isEmpty() -> HomeUiState.Empty(
                query = _searchQuery.value,
                severity = _selectedSeverity.value
            )
            else -> HomeUiState.Success(
                notes = notes,
                query = _searchQuery.value,
                severity = _selectedSeverity.value,
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
    
    fun onSeveritySelected(severity: VulnSeverity?) {
        _selectedSeverity.value = severity
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
    
    fun deleteNotes(noteIds: List<Long>) {
        viewModelScope.launch {
            repository.deleteNotes(noteIds)
        }
    }
}

sealed interface HomeUiState {
    data object Loading : HomeUiState
    
    data class Success(
        val notes: List<Note>,
        val query: String = "",
        val severity: VulnSeverity? = null,
        val sortBy: NoteSortBy = NoteSortBy.UPDATED_DESC
    ) : HomeUiState
    
    data class Empty(
        val query: String = "",
        val severity: VulnSeverity? = null
    ) : HomeUiState
    
    data class Error(val message: String) : HomeUiState
}
