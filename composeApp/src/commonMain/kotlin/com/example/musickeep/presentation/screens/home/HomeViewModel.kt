package com.example.musickeep.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musickeep.domain.model.Music
import com.example.musickeep.domain.repository.MusicRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val musicList: List<Music> = emptyList(),
    val searchQuery: String = "",
    val selectedGenre: String? = null,
    val isLoading: Boolean = false,
    val genres: List<String> = listOf("Pop", "Rock", "Jazz", "Hip Hop", "RnB")
)

class HomeViewModel(
    private val repository: MusicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadMusic()
    }

    private fun loadMusic() {
        repository.getAllMusic()
            .onStart { _uiState.update { it.copy(isLoading = true) } }
            .onEach { list -> 
                _uiState.update { it.copy(musicList = list, isLoading = false) } 
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        
        // Implementasi Debounce: Tunggu 300ms sebelum melakukan query ke database
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            executeSearch()
        }
    }

    fun onGenreSelect(genre: String?) {
        _uiState.update { it.copy(selectedGenre = if (it.selectedGenre == genre) null else genre) }
        executeSearch()
    }

    private fun executeSearch() {
        val query = _uiState.value.searchQuery
        val genre = _uiState.value.selectedGenre

        if (query.isBlank() && genre == null) {
            loadMusic()
        } else {
            repository.searchMusic(query)
                .onEach { list -> 
                    val filteredList = if (genre != null) {
                        list.filter { it.genre?.contains(genre, ignoreCase = true) == true }
                    } else {
                        list
                    }
                    _uiState.update { it.copy(musicList = filteredList) } 
                }
                .launchIn(viewModelScope)
        }
    }

    fun deleteMusic(id: Long) {
        viewModelScope.launch {
            repository.deleteMusic(id)
        }
    }
}
