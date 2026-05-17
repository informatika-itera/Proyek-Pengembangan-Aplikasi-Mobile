package com.soundletter.app.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soundletter.app.domain.model.Note
import com.soundletter.app.domain.repository.MusicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchState(
    val query: String = "",
    val results: List<Note> = emptyList(),
    val isLoading: Boolean = false
)

class SearchScreenViewModel(
    private val musicRepository: MusicRepository
) : ViewModel() {
    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    fun onQueryChange(newQuery: String) {
        _state.update { it.copy(query = newQuery) }
        search(newQuery)
    }

    private fun search(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _state.update { it.copy(results = emptyList()) }
                return@launch
            }
            _state.update { it.copy(isLoading = true) }
            // Simulasi hasil pencarian
            _state.update { it.copy(isLoading = false) }
        }
    }
}
