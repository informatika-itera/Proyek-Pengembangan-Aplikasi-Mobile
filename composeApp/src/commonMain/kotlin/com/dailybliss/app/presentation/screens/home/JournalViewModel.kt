package com.dailybliss.app.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailybliss.app.domain.model.Moment
import com.dailybliss.app.domain.usecase.GetAllMomentsUseCase
import com.dailybliss.app.domain.usecase.MomentSortBy
import com.dailybliss.app.domain.usecase.SaveMomentUseCase
import com.dailybliss.app.domain.usecase.SearchMomentsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class JournalViewModel(
    private val getAllMomentsUseCase: GetAllMomentsUseCase,
    private val searchMomentsUseCase: SearchMomentsUseCase,
    private val saveMomentUseCase: SaveMomentUseCase
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val _sortBy = MutableStateFlow(MomentSortBy.UPDATED_DESC)
    val sortBy = _sortBy.asStateFlow()

    val uiState: StateFlow<JournalUiState> = combine(
        getAllMomentsUseCase(),
        _query,
        _sortBy
    ) { moments, query, sort ->
        var filtered = moments
        
        if (query.isNotBlank()) {
            filtered = filtered.filter { 
                it.title.contains(query, ignoreCase = true) || 
                it.content.contains(query, ignoreCase = true) 
            }
        }
        
        filtered = when (sort) {
            MomentSortBy.TITLE_ASC -> filtered.sortedBy { it.title.lowercase() }
            MomentSortBy.TITLE_DESC -> filtered.sortedByDescending { it.title.lowercase() }
            MomentSortBy.CREATED_ASC -> filtered.sortedBy { it.createdAt }
            MomentSortBy.CREATED_DESC -> filtered.sortedByDescending { it.createdAt }
            MomentSortBy.UPDATED_ASC -> filtered.sortedBy { it.updatedAt }
            MomentSortBy.UPDATED_DESC -> filtered.sortedByDescending { it.updatedAt }
        }

        if (filtered.isEmpty()) {
            JournalUiState.Empty(query)
        } else {
            JournalUiState.Success(filtered, query)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = JournalUiState.Loading
    )

    fun onSearchQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    fun onSortByChanged(sort: MomentSortBy) {
        _sortBy.value = sort
    }

    fun togglePin(momentId: Long) {
        viewModelScope.launch {
            // Logic to toggle pin if needed
        }
    }

    fun clearSearch() {
        _query.value = ""
    }
}

sealed interface JournalUiState {
    data object Loading : JournalUiState
    
    data class Success(
        val moments: List<Moment>,
        val query: String = ""
    ) : JournalUiState
    
    data class Empty(
        val query: String = ""
    ) : JournalUiState
    
    data class Error(val message: String) : JournalUiState
}

