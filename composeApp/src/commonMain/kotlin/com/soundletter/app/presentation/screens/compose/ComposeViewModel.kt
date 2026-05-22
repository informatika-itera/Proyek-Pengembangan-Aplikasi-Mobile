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
    val selectedSong: SongSuggestion? = null,
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

    fun onRecipientChange(value: String) = _state.update { it.copy(recipient = value) }
    fun onSenderChange(value: String) = _state.update { it.copy(sender = value) }
    fun onMessageChange(value: String) = _state.update { it.copy(message = value) }
    fun onSongSelect(song: SongSuggestion) = _state.update { it.copy(selectedSong = song) }

    /**
     * Mengirim SoundLetter: Simpan ke Firestore (Public) & SQLDelight (Local History)
     */
    fun sendSoundLetter() {
        val currentState = _state.value
        
        // 1. Validasi Input
        if (currentState.recipient.isBlank() || currentState.message.isBlank()) {
            _state.update { it.copy(sendStatus = UiState.Error("Recipient and message cannot be empty")) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(sendStatus = UiState.Loading) }
            try {
                val newLetter = Note(
                    recipient = currentState.recipient,
                    sender = if (currentState.sender.isBlank()) "Anon" else currentState.sender,
                    content = currentState.message,
                    songTitle = currentState.selectedSong?.title,
                    songArtist = currentState.selectedSong?.artist,
                    createdAt = Clock.System.now(),
                    updatedAt = Clock.System.now()
                )

                // 2. Dual-Save via Repository (Lokal + simulated Remote)
                letterRepository.sendLetter(newLetter)

                _state.update { it.copy(sendStatus = UiState.Success(Unit)) }
            } catch (e: Exception) {
                _state.update { it.copy(sendStatus = UiState.Error(e.message ?: "An unexpected error occurred")) }
            }
        }
    }

    fun recommendSongs() {
        viewModelScope.launch {
            _state.update { it.copy(isAiLoading = true) }
            // Simulation AI Gemini recommendation
            kotlinx.coroutines.delay(1000)
            _state.update { 
                it.copy(
                    suggestions = listOf(
                        SongSuggestion("Starboy", "The Weeknd"),
                        SongSuggestion("Midnight City", "M83"),
                        SongSuggestion("Blinding Lights", "The Weeknd")
                    ),
                    isAiLoading = false
                )
            }
        }
    }
    
    fun resetStatus() = _state.update { it.copy(sendStatus = UiState.Idle) }
}
