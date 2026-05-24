package com.example.musickeep.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musickeep.domain.model.Music
import com.example.musickeep.domain.repository.MusicRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val musicList: List<Music> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

class HomeViewModel(
    private val repository: MusicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

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
        if (query.isBlank()) {
            loadMusic()
        } else {
            repository.searchMusic(query)
                .onEach { list -> _uiState.update { it.copy(musicList = list) } }
                .launchIn(viewModelScope)
        }
    }

    fun deleteMusic(id: Long) {
        viewModelScope.launch {
            repository.deleteMusic(id)
        }
    }
}
