package com.soundletter.app.presentation.screens.compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soundletter.app.core.network.GeminiService
import com.soundletter.app.core.util.UiState
import com.soundletter.app.domain.model.Note
import com.soundletter.app.domain.repository.LetterRepository
import com.soundletter.app.domain.repository.MusicRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

data class SongSuggestion(
    val title: String, 
    val artist: String,
    val previewUrl: String? = null,
    val albumArtUrl: String? = null
)

data class ComposeState(
    val recipient: String = "",
    val sender: String = "",
    val message: String = "",
    val selectedSong: SongSuggestion? = null,
    val suggestions: List<SongSuggestion> = emptyList(),
    val isAiLoading: Boolean = false,
    val sendStatus: UiState<Boolean> = UiState.Idle
)

sealed class ComposeUiEvent {
    object ShowOfflineSnackbar : ComposeUiEvent()
    data class ShowError(val message: String) : ComposeUiEvent()
}

class ComposeViewModel(
    private val letterRepository: LetterRepository,
    private val musicRepository: MusicRepository,
    private val geminiService: GeminiService
) : ViewModel() {
    private val _state = MutableStateFlow(ComposeState())
    val state: StateFlow<ComposeState> = _state.asStateFlow()

    // Menambahkan extraBufferCapacity agar event tidak hilang saat diuji
    private val _uiEvent = MutableSharedFlow<ComposeUiEvent>(extraBufferCapacity = 1)
    val uiEvent = _uiEvent.asSharedFlow()

    fun onRecipientChange(value: String) = _state.update { it.copy(recipient = value) }
    fun onSenderChange(value: String) = _state.update { it.copy(sender = value) }
    fun onMessageChange(value: String) = _state.update { it.copy(message = value) }
    
    fun onSongSelect(song: SongSuggestion) {
        _state.update { it.copy(selectedSong = song) }
    }

    fun sendSoundLetter() {
        val currentState = _state.value
        if (currentState.recipient.isBlank() || currentState.message.isBlank()) {
            _state.update { it.copy(sendStatus = UiState.Error("Penerima dan pesan tidak boleh kosong")) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(sendStatus = UiState.Loading) }
            try {
                val newLetter = Note(
                    recipient = currentState.recipient,
                    sender = if (currentState.sender.isBlank()) "Anonim" else currentState.sender,
                    content = currentState.message,
                    songTitle = currentState.selectedSong?.title,
                    songArtist = currentState.selectedSong?.artist,
                    songPreviewUrl = currentState.selectedSong?.previewUrl,
                    songAlbumArtUrl = currentState.selectedSong?.albumArtUrl,
                    createdAt = Clock.System.now(),
                    updatedAt = Clock.System.now()
                )

                val isSynced = letterRepository.sendLetter(newLetter)
                
                if (!isSynced) {
                    _uiEvent.emit(ComposeUiEvent.ShowOfflineSnackbar)
                }

                _state.update { it.copy(sendStatus = UiState.Success(isSynced)) }
            } catch (e: Exception) {
                _state.update { it.copy(sendStatus = UiState.Error(e.message ?: "Gagal mengirim surat")) }
            }
        }
    }

    fun recommendSongs() {
        val messageText = _state.value.message
        if (messageText.isBlank()) {
            _uiEvent.tryEmit(ComposeUiEvent.ShowError("Tulis pesanmu dulu!"))
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isAiLoading = true, suggestions = emptyList()) }
            try {
                // 1. Ambil saran Mood/Genre dari Gemini
                val rawTags = geminiService.getSongRecommendations(messageText)
                
                // 2. Cari lagu berdasarkan Mood di Jamendo
                val musicResults = musicRepository.searchSongs(rawTags)
                
                if (musicResults.isEmpty()) {
                    _uiEvent.emit(ComposeUiEvent.ShowError("Gagal memuat lagu. Coba lagi nanti."))
                    _state.update { it.copy(isAiLoading = false) }
                    return@launch
                }

                val newSuggestions = musicResults.map { 
                    SongSuggestion(it.title, it.artist, it.previewUrl, it.albumArtUrl) 
                }

                _state.update { 
                    it.copy(
                        suggestions = newSuggestions,
                        selectedSong = newSuggestions.firstOrNull(),
                        isAiLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isAiLoading = false) }
                _uiEvent.emit(ComposeUiEvent.ShowError("Koneksi API bermasalah. Menggunakan data cadangan."))
            }
        }
    }
    
    fun resetStatus() = _state.update { it.copy(sendStatus = UiState.Idle) }
}
