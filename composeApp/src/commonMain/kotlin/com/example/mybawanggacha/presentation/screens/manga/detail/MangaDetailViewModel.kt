package com.example.mybawanggacha.presentation.screens.manga.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybawanggacha.domain.library.model.MediaType
import com.example.mybawanggacha.domain.library.repository.LibraryRepository
import com.example.mybawanggacha.domain.manga.repository.MangaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MangaDetailViewModel(
    private val mangaRepository: MangaRepository,
    private val libraryRepository: LibraryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MangaDetailUiState>(MangaDetailUiState.Loading)
    val uiState: StateFlow<MangaDetailUiState> = _uiState.asStateFlow()

    fun fetchMangaDetail(malId: Int) {
        loadMangaDetail(
            malId = malId,
            keepCurrentContent = false,
            forceRefresh = false
        )
    }

    fun refreshMangaDetail(malId: Int) {
        loadMangaDetail(
            malId = malId,
            keepCurrentContent = true,
            forceRefresh = true
        )
    }

    private fun loadMangaDetail(
        malId: Int,
        keepCurrentContent: Boolean,
        forceRefresh: Boolean
    ) {
        val currentSuccess = _uiState.value as? MangaDetailUiState.Success
        val canKeepContent = keepCurrentContent && currentSuccess?.manga?.malId == malId

        if (canKeepContent && currentSuccess?.isRefreshing == true) return

        viewModelScope.launch {
            val staleState = _uiState.value as? MangaDetailUiState.Success
            val shouldKeepStaleState = keepCurrentContent && staleState?.manga?.malId == malId

            if (shouldKeepStaleState && staleState != null) {
                _uiState.value = staleState.copy(isRefreshing = true)
            } else {
                _uiState.value = MangaDetailUiState.Loading
            }

            runCatching {
                val manga = mangaRepository.getMangaDetail(
                    malId = malId,
                    forceRefresh = forceRefresh
                )
                val existingEntry = libraryRepository.getEntry(
                    mediaId = manga.malId,
                    mediaType = MediaType.Manga
                )

                manga to existingEntry
            }.onSuccess { (manga, existingEntry) ->
                _uiState.value = MangaDetailUiState.Success(
                    manga = manga,
                    libraryEntryId = existingEntry?.id,
                    isRefreshing = false
                )
            }.onFailure { error ->
                val latestState = _uiState.value as? MangaDetailUiState.Success
                if (shouldKeepStaleState && latestState != null) {
                    _uiState.value = latestState.copy(isRefreshing = false)
                } else {
                    _uiState.value = MangaDetailUiState.Error(
                        error.message ?: "Unknown error occurred"
                    )
                }
            }
        }
    }
}