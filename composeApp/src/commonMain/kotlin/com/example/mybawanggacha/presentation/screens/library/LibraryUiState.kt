package com.example.mybawanggacha.presentation.screens.library

import com.example.mybawanggacha.domain.model.LibraryEntry
import com.example.mybawanggacha.domain.model.LibraryStatus
import com.example.mybawanggacha.domain.model.MediaType

sealed interface LibraryUiState {
    data object Loading : LibraryUiState
    data class Empty(
        val selectedStatus: LibraryStatus?
    ) : LibraryUiState
    data class Success(
        val entries: List<LibraryEntry>,
        val selectedStatus: LibraryStatus?
    ) : LibraryUiState
    data class Error(val message: String) : LibraryUiState
}

data class LibraryEntryEditorUiState(
    val entryId: Long? = null,
    val mediaId: Int = 0,
    val mediaType: MediaType = MediaType.Anime,
    val title: String = "",
    val imageUrl: String? = null,
    val status: LibraryStatus = LibraryStatus.PlanToWatch,
    val progressText: String = "0",
    val totalText: String = "",
    val scoreText: String = "",
    val notes: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val errorMessage: String? = null
)
