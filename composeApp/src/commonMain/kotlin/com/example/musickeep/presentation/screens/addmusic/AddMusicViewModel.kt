package com.example.musickeep.presentation.screens.addmusic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musickeep.domain.model.Music
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddMusicUiState(
    val title: String = "",
    val artist: String = "",
    val genre: String = "",
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)

class AddMusicViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AddMusicUiState())
    val uiState: StateFlow<AddMusicUiState> = _uiState.asStateFlow()

    fun onTitleChange(newTitle: String) {
        _uiState.update { it.copy(title = newTitle) }
    }

    fun onArtistChange(newArtist: String) {
        _uiState.update { it.copy(artist = newArtist) }
    }

    fun onGenreChange(newGenre: String) {
        _uiState.update { it.copy(genre = newGenre) }
    }

    fun saveMusic() {
        val currentState = _uiState.value
        if (currentState.title.isBlank() || currentState.artist.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Judul dan Artis wajib diisi!") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Simulating save for now until Repository is ready
            kotlinx.coroutines.delay(1000) 
            _uiState.update { it.copy(isLoading = false, isSaved = true) }
        }
    }
}
