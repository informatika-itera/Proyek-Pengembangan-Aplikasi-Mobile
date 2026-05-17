package com.soundletter.app.presentation.screens.compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soundletter.app.core.util.UiState
import com.soundletter.app.domain.model.Note
import com.soundletter.app.domain.repository.LetterRepository
import com.soundletter.app.domain.repository.MusicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

data class SongSuggestion(val title: String, val artist: String)

data class ComposeState(
    val recipient: String = "",
    val sender: String = "",
    val message: String = "",
    val suggestions: List<SongSuggestion> = emptyList(),
    val isAiLoading: Boolean = false,
    val sendStatus: UiState<Unit> = UiState.Idle
)

class ComposeViewModel(
    private val letterRepository: LetterRepository,
    private val musicRepository: MusicRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ComposeState())
    val state: StateFlow<ComposeState> = _state.asStateFlow()

    fun onToChange(value: String) { _state.update { it.copy(recipient = value) } }
    fun onFromChange(value: String) { _state.update { it.copy(sender = value) } }
    fun onMessageChange(value: String) { _state.update { it.copy(message = value) } }

    fun recommendSongs() {
        viewModelScope.launch {
            _state.update { it.copy(isAiLoading = true) }
            // Simulation AI Gemini
            _state.update { 
                it.copy(
                    suggestions = listOf(
                        SongSuggestion("Starboy", "The Weeknd"),
                        SongSuggestion("Midnight City", "M83")
                    ),
                    isAiLoading = false
                )
            }
        }
    }

    fun sendSoundLetter(to: String, from: String, message: String, songTitle: String?) {
        viewModelScope.launch {
            _state.update { it.copy(sendStatus = UiState.Loading) }
            try {
                val newNote = Note(
                    recipient = to,
                    sender = if (from.isBlank()) "Anon" else from,
                    content = message,
                    songTitle = songTitle,
                    createdAt = Clock.System.now(),
                    updatedAt = Clock.System.now()
                )

                // 1. Simpan ke Lokal (History) via Repository
                letterRepository.sendLetter(newNote)

                // 2. TODO: Simpan ke Firebase Firestore (Remote)
                // Implementasi Firestore SDK di KMP biasanya membutuhkan wrapper atau expect/actual
                // Simulasi delay network
                kotlinx.coroutines.delay(1000)

                _state.update { it.copy(sendStatus = UiState.Success(Unit)) }
            } catch (e: Exception) {
                _state.update { it.copy(sendStatus = UiState.Error(e.message ?: "Failed to send")) }
            }
        }
    }
    
    fun resetStatus() {
        _state.update { it.copy(sendStatus = UiState.Idle) }
    }
}
