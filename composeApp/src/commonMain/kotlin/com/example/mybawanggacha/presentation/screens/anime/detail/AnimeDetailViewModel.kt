package com.example.mybawanggacha.presentation.screens.anime.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybawanggacha.domain.anime.model.AnimeDetail
import com.example.mybawanggacha.domain.library.model.LibraryEntry
import com.example.mybawanggacha.domain.library.model.LibraryStatus
import com.example.mybawanggacha.domain.library.model.MediaType
import com.example.mybawanggacha.domain.library.model.UserProgress
import com.example.mybawanggacha.domain.anime.repository.AnimeRepository
import com.example.mybawanggacha.domain.library.repository.LibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AnimeDetailViewModel(
    private val animeRepository: AnimeRepository,
    private val libraryRepository: LibraryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AnimeDetailUiState>(AnimeDetailUiState.Loading)
    val uiState: StateFlow<AnimeDetailUiState> = _uiState.asStateFlow()

    fun fetchAnimeDetail(malId: Int) {
        viewModelScope.launch {
            _uiState.value = AnimeDetailUiState.Loading

            runCatching {
                val detail = animeRepository.getAnimeDetail(malId)
                val existingEntry = libraryRepository.getEntry(
                    mediaId = detail.anime.malId,
                    mediaType = MediaType.Anime
                )

                detail to existingEntry
            }.onSuccess { (detail, existingEntry) ->
                _uiState.value = AnimeDetailUiState.Success(
                    anime = detail.anime,
                    episodes = detail.episodes,
                    libraryEntryId = existingEntry?.id
                )
            }.onFailure { error ->
                _uiState.value = AnimeDetailUiState.Error(
                    error.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun setEpisodeWatched(
        episodeNumber: Int,
        watched: Boolean
    ) {
        val currentState = _uiState.value as? AnimeDetailUiState.Success ?: return

        viewModelScope.launch {
            val animeId = currentState.anime.malId

            runCatching {
                animeRepository.setEpisodeWatched(
                    animeId = animeId,
                    episodeNumber = episodeNumber,
                    watched = watched
                )
            }.onSuccess {
                val latestState = _uiState.value as? AnimeDetailUiState.Success ?: return@onSuccess
                if (latestState.anime.malId != animeId) return@onSuccess

                val updatedEpisodes = latestState.episodes.map { episode ->
                    if (episode.number == episodeNumber) {
                        episode.copy(watched = watched)
                    } else {
                        episode
                    }
                }

                _uiState.value = latestState.copy(episodes = updatedEpisodes)

                val savedEntryId = syncLibraryProgressFromEpisodes(
                    anime = latestState.anime,
                    watchedCount = updatedEpisodes.count { it.watched },
                    totalEpisodes = latestState.anime.episodes ?: updatedEpisodes.size.takeIf { it > 0 }
                )

                if (savedEntryId != null) {
                    val refreshedState = _uiState.value as? AnimeDetailUiState.Success ?: return@onSuccess
                    _uiState.value = refreshedState.copy(libraryEntryId = savedEntryId)
                }
            }
        }
    }

    private suspend fun syncLibraryProgressFromEpisodes(
        anime: AnimeDetail,
        watchedCount: Int,
        totalEpisodes: Int?
    ): Long? {
        val existingEntry = libraryRepository.getEntry(
            mediaId = anime.malId,
            mediaType = MediaType.Anime
        )

        val status = when {
            watchedCount <= 0 -> existingEntry?.status ?: LibraryStatus.PlanToWatch
            totalEpisodes != null && totalEpisodes > 0 && watchedCount >= totalEpisodes -> LibraryStatus.Completed
            else -> LibraryStatus.Watching
        }

        return runCatching {
            libraryRepository.upsertEntry(
                LibraryEntry(
                    id = existingEntry?.id ?: 0L,
                    mediaId = anime.malId,
                    mediaType = MediaType.Anime,
                    title = anime.title,
                    imageUrl = anime.imageUrl,
                    status = status,
                    progress = UserProgress(
                        current = watchedCount,
                        total = totalEpisodes
                    ),
                    userScore = existingEntry?.userScore,
                    notes = existingEntry?.notes
                )
            )
        }.getOrNull()
    }
}