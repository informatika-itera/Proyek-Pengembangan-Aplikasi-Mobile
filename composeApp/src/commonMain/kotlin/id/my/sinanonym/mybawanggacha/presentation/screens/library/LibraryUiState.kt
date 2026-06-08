package id.my.sinanonym.mybawanggacha.presentation.screens.library

import id.my.sinanonym.mybawanggacha.domain.library.model.LibraryEntry
import id.my.sinanonym.mybawanggacha.domain.library.model.LibraryStatus
import id.my.sinanonym.mybawanggacha.domain.library.model.MediaType

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
