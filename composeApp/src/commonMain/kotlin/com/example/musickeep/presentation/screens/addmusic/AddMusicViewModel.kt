package com.example.musickeep.presentation.screens.addmusic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musickeep.domain.model.Music
import com.example.musickeep.domain.repository.MusicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddMusicUiState(
    val id: Long? = null,
    val title: String = "",
    val artist: String = "",
    val genre: String = "",
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)

class AddMusicViewModel(
    private val repository: MusicRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddMusicUiState())
    val uiState: StateFlow<AddMusicUiState> = _uiState.asStateFlow()

    fun loadMusic(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val music = repository.getMusicById(id)
            if (music != null) {
                _uiState.update {
                    it.copy(
                        id = music.id,
                        title = music.title,
                        artist = music.artist,
                        genre = music.genre ?: "",
                        isLoading = false
                    )
                }
            }
        }
    }

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
            try {
                val music = Music(
                    id = currentState.id,
                    title = currentState.title,
                    artist = currentState.artist,
                    genre = currentState.genre
                )
                
                if (music.id == null) {
                    repository.insertMusic(music)
                } else {
                    repository.updateMusic(music)
                }

                _uiState.update { it.copy(isLoading = false, isSaved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
