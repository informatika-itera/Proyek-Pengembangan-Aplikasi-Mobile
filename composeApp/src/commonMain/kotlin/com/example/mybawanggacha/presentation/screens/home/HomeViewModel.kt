package com.example.mybawanggacha.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybawanggacha.domain.model.Note
import com.example.mybawanggacha.domain.model.NoteCategory
import com.example.mybawanggacha.domain.repository.NoteRepository
import com.example.mybawanggacha.domain.usecase.DeleteNoteUseCase
import com.example.mybawanggacha.domain.usecase.GetAllNotesUseCase
import com.example.mybawanggacha.domain.usecase.NoteSortBy
import com.example.mybawanggacha.domain.usecase.SearchNotesUseCase
import com.example.mybawanggacha.data.remote.dto.AnimeEntry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val searchNotesUseCase: SearchNotesUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val repository: NoteRepository,
    private val jikanService: com.example.mybawanggacha.data.remote.api.JikanService
) : ViewModel() {
    
    private val _animeRecommendations = MutableStateFlow<List<AnimeEntry>>(emptyList())
    val animeRecommendations: StateFlow<List<AnimeEntry>> = _animeRecommendations.asStateFlow()

    init {
        fetchJikanData()
    }

    private fun fetchJikanData() {
        viewModelScope.launch {
            try {
                val response = jikanService.fetch()
                val allEntries = response.data.flatMap { it.entry }
                _animeRecommendations.value = allEntries
            } catch (e: Exception) {
                println("Jikan error: ${e.message}")
            }
        }
    }

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow<NoteCategory?>(null)
    private val _sortBy = MutableStateFlow(NoteSortBy.UPDATED_DESC)
    private val _isLoading = MutableStateFlow(false)
    
    private val debouncedSearchQuery = _searchQuery.debounce(300)
    
    val sortBy: StateFlow<NoteSortBy> = _sortBy
    
    val uiState: StateFlow<HomeUiState> = combine(
        debouncedSearchQuery,
        _selectedCategory,
        _sortBy
    ) { query, category, sortBy ->
        Triple(query, category, sortBy)
    }.flatMapLatest { (query, category, sortBy) ->
        if (query.isBlank() && category == null) {
            getAllNotesUseCase(sortBy)
        } else {
            searchNotesUseCase(query, category)
        }
    }.combine(_isLoading) { notes, isLoading ->
        when {
            isLoading -> HomeUiState.Loading
            notes.isEmpty() -> HomeUiState.Empty(
                query = _searchQuery.value,
                category = _selectedCategory.value
            )
            else -> HomeUiState.Success(
                notes = notes,
                query = _searchQuery.value,
                category = _selectedCategory.value,
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
    
    fun onCategorySelected(category: NoteCategory?) {
        _selectedCategory.value = category
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
        val category: NoteCategory? = null,
        val sortBy: NoteSortBy = NoteSortBy.UPDATED_DESC
    ) : HomeUiState
    
    data class Empty(
        val query: String = "",
        val category: NoteCategory? = null
    ) : HomeUiState
    
    data class Error(val message: String) : HomeUiState
}
