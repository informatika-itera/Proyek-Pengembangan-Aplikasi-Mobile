package com.example.foodsaver.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodsaver.domain.model.Note
import com.example.foodsaver.domain.model.NoteCategory
import com.example.foodsaver.domain.repository.NoteRepository
import com.example.foodsaver.domain.usecase.GetAllNotesUseCase
import com.example.foodsaver.domain.usecase.NoteSortBy
import com.example.foodsaver.domain.usecase.SearchNotesUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val notes: List<Note>,
        val query: String = "",
        val category: NoteCategory? = null
    ) : HomeUiState
    data class Empty(
        val query: String = "",
        val category: NoteCategory? = null
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

@OptIn(FlowPreview::class)
class HomeViewModel(
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val searchNotesUseCase: SearchNotesUseCase,
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow<NoteCategory?>(null)
    private val _sortBy = MutableStateFlow(NoteSortBy.UPDATED_DESC)
    val sortBy = _sortBy.asStateFlow()

    val uiState: StateFlow<HomeUiState> = combine(
        _searchQuery.debounce(300),
        _selectedCategory,
        _sortBy
    ) { query, category, sort ->
        Triple(query, category, sort)
    }.flatMapLatest { (query, category, sort) ->
        searchNotesUseCase(query, category).combine(
            getAllNotesUseCase(sort)
        ) { searchResult, allNotes ->
            // If searching/filtering, use searchResult. Otherwise use sorted allNotes.
            val displayNotes = if (query.isBlank() && category == null) {
                allNotes
            } else {
                searchResult
            }

            if (displayNotes.isEmpty()) {
                HomeUiState.Empty(query, category)
            } else {
                HomeUiState.Success(displayNotes, query, category)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState.Loading
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: NoteCategory?) {
        _selectedCategory.value = category
    }

    fun onSortByChanged(sortBy: NoteSortBy) {
        _sortBy.value = sortBy
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _selectedCategory.value = null
    }

    fun togglePin(id: Long) {
        viewModelScope.launch {
            noteRepository.togglePinNote(id)
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            noteRepository.deleteNote(id)
        }
    }
}
