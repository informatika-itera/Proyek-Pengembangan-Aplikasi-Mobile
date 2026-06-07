package com.soundletter.app.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soundletter.app.core.audio.AudioPlayer
import com.soundletter.app.core.util.UiState
import com.soundletter.app.domain.model.Note
import com.soundletter.app.domain.repository.LetterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailState(
    val letterState: UiState<Note> = UiState.Idle,
    val isPlaying: Boolean = false
)

class DetailMessageScreenViewModel(
    private val letterRepository: LetterRepository,
    private val audioPlayer: AudioPlayer
) : ViewModel() {
    private val _state = MutableStateFlow(DetailState())
    val state: StateFlow<DetailState> = _state.asStateFlow()

    fun loadMessage(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(letterState = UiState.Loading) }
            try {
                val noteId = id.toLongOrNull() ?: 0L
                val result = letterRepository.getLetterById(noteId)
                if (result != null) {
                    _state.update { it.copy(letterState = UiState.Success(result)) }
                } else {
                    _state.update { it.copy(letterState = UiState.Error("Letter not found")) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(letterState = UiState.Error(e.message ?: "An unexpected error occurred")) }
            }
        }
    }

    fun toggleAudio(url: String?) {
        if (url.isNullOrBlank()) return
        
        if (_state.value.isPlaying) {
            audioPlayer.pause()
            _state.update { it.copy(isPlaying = false) }
        } else {
            // Fix: Gunakan callback onFinished agar icon Play/Pause di UI sinkron
            audioPlayer.play(url) {
                _state.update { it.copy(isPlaying = false) }
            }
            _state.update { it.copy(isPlaying = true) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.stop()
        audioPlayer.release()
    }
}
