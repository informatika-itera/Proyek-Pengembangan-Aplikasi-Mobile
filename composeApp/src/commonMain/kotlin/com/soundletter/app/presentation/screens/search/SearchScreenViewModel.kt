package com.soundletter.app.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soundletter.app.core.util.UiState
import com.soundletter.app.domain.model.Note
import com.soundletter.app.domain.repository.LetterRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SearchScreenViewModel(
    private val letterRepository: LetterRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _searchState = MutableStateFlow<UiState<List<Note>>>(UiState.Idle)
    val searchState: StateFlow<UiState<List<Note>>> = _searchState.asStateFlow()

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
        if (newQuery.isBlank()) {
            _searchState.value = UiState.Idle
            return
        }
        searchRecipient(newQuery)
    }

    private fun searchRecipient(name: String) {
        viewModelScope.launch {
            _searchState.value = UiState.Loading
            // Menggunakan flow dari repository untuk pencarian real-time
            letterRepository.getLetters()
                .map { letters ->
                    letters.filter { it.recipient.contains(name, ignoreCase = true) }
                }
                .catch { e ->
                    _searchState.value = UiState.Error(e.message ?: "Search failed")
                }
                .collect { results ->
                    _searchState.value = UiState.Success(results)
                }
        }
    }
}
