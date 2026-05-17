package com.example.mybawanggacha.presentation.screens.library.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybawanggacha.domain.library.model.LibraryEntry
import com.example.mybawanggacha.domain.library.model.LibraryStatus
import com.example.mybawanggacha.domain.library.model.MediaType
import com.example.mybawanggacha.domain.library.model.UserProgress
import com.example.mybawanggacha.domain.library.model.UserScore
import com.example.mybawanggacha.domain.library.repository.LibraryRepository
import com.example.mybawanggacha.presentation.screens.library.LibraryEntryEditorUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LibraryEntryEditorViewModel(
    private val libraryRepository: LibraryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryEntryEditorUiState())
    val uiState: StateFlow<LibraryEntryEditorUiState> = _uiState.asStateFlow()

    fun start(
        mediaId: Int,
        mediaType: MediaType,
        title: String,
        imageUrl: String?,
        totalCount: Int?,
        entryId: Long?
    ) {
        val current = _uiState.value
        if (current.mediaId == mediaId && current.mediaType == mediaType && current.title.isNotBlank()) return

        _uiState.value = LibraryEntryEditorUiState(
            mediaId = mediaId,
            mediaType = mediaType,
            title = title,
            imageUrl = imageUrl,
            totalText = totalCount?.takeIf { it > 0 }?.toString().orEmpty(),
            isLoading = true
        )

        viewModelScope.launch {
            runCatching {
                entryId?.let { libraryRepository.getEntryById(it) }
                    ?: libraryRepository.getEntry(mediaId, mediaType)
            }.onSuccess { existingEntry ->
                if (existingEntry == null) {
                    _uiState.update { it.copy(isLoading = false) }
                } else {
                    _uiState.value = existingEntry.toEditorState()
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Gagal memuat item list"
                    )
                }
            }
        }
    }

    fun updateStatus(status: LibraryStatus) {
        _uiState.update { it.copy(status = status, errorMessage = null) }
    }

    fun updateProgress(value: String) {
        _uiState.update { it.copy(progressText = value.filter { char -> char.isDigit() }, errorMessage = null) }
    }

    fun updateTotal(value: String) {
        _uiState.update { it.copy(totalText = value.filter { char -> char.isDigit() }, errorMessage = null) }
    }

    fun updateScore(value: String) {
        _uiState.update { it.copy(scoreText = value.filter { char -> char.isDigit() }.take(2), errorMessage = null) }
    }

    fun updateNotes(value: String) {
        _uiState.update { it.copy(notes = value, errorMessage = null) }
    }

    fun save() {
        val state = _uiState.value
        val progress = state.progressText.toIntOrNull() ?: 0
        val total = state.totalText.toIntOrNull()
        val score = state.scoreText.toIntOrNull()

        val validationError = validate(
            title = state.title,
            progress = progress,
            total = total,
            score = score
        )

        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            runCatching {
                libraryRepository.upsertEntry(
                    LibraryEntry(
                        id = state.entryId ?: 0L,
                        mediaId = state.mediaId,
                        mediaType = state.mediaType,
                        title = state.title.trim(),
                        imageUrl = state.imageUrl,
                        status = state.status,
                        progress = UserProgress(
                            current = progress,
                            total = total
                        ),
                        userScore = score?.let(::UserScore),
                        notes = state.notes.trim().takeIf { it.isNotBlank() }
                    )
                )
            }.onSuccess {
                _uiState.update { it.copy(isSaving = false, saved = true) }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = error.message ?: "Gagal menyimpan item"
                    )
                }
            }
        }
    }

    private fun validate(
        title: String,
        progress: Int,
        total: Int?,
        score: Int?
    ): String? {
        return when {
            title.isBlank() -> "Judul tidak boleh kosong"
            progress < 0 -> "Progress tidak boleh negatif"
            total != null && total < 0 -> "Total episode/chapter tidak boleh negatif"
            total != null && progress > total -> "Progress tidak boleh lebih besar dari total"
            score != null && score !in 1..10 -> "Score pribadi harus berada di rentang 1 sampai 10"
            else -> null
        }
    }
}

private fun LibraryEntry.toEditorState(): LibraryEntryEditorUiState {
    return LibraryEntryEditorUiState(
        entryId = id,
        mediaId = mediaId,
        mediaType = mediaType,
        title = title,
        imageUrl = imageUrl,
        status = status,
        progressText = progress.current.toString(),
        totalText = progress.total?.toString().orEmpty(),
        scoreText = userScore?.value?.toString().orEmpty(),
        notes = notes.orEmpty(),
        isLoading = false
    )
}
