package com.soundletter.app.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soundletter.app.presentation.screens.home.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SearchState(
    val query: String = "",
    val results: List<Message> = emptyList(),
    val isLoading: Boolean = false
)

class SearchViewModel : ViewModel() {
    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    fun onQueryChange(newQuery: String) {
        _state.value = _state.value.copy(query = newQuery)
        search(newQuery)
    }

    private fun search(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _state.value = _state.value.copy(results = emptyList())
                return@launch
            }
            _state.value = _state.value.copy(isLoading = true)
            _state.value = _state.value.copy(
                results = listOf(
                    Message("1", query, "Anon", "Pesan untuk $query", "Music Link")
                ),
                isLoading = false
            )
        }
    }
}
