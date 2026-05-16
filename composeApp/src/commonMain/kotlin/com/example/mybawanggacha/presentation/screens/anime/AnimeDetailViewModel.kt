package com.example.mybawanggacha.presentation.screens.anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybawanggacha.domain.model.AnimeDetail
import com.example.mybawanggacha.domain.model.LibraryEntry
import com.example.mybawanggacha.domain.model.LibraryStatus
import com.example.mybawanggacha.domain.model.MediaType
import com.example.mybawanggacha.domain.model.UserProgress
import com.example.mybawanggacha.domain.repository.AnimeRepository
import com.example.mybawanggacha.domain.repository.LibraryRepository
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
                animeRepository.getAnimeDetail(malId)
            }.onSuccess { detail ->
                _uiState.value = AnimeDetailUiState.Success(
                    anime = detail.anime,
                    episodes = detail.episodes
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

                syncLibraryProgressFromEpisodes(
                    anime = latestState.anime,
                    watchedCount = updatedEpisodes.count { it.watched },
                    totalEpisodes = latestState.anime.episodes ?: updatedEpisodes.size.takeIf { it > 0 }
                )
            }
        }
    }

    private suspend fun syncLibraryProgressFromEpisodes(
        anime: AnimeDetail,
        watchedCount: Int,
        totalEpisodes: Int?
    ) {
        val existingEntry = libraryRepository.getEntry(
            mediaId = anime.malId,
            mediaType = MediaType.Anime
        )

        val status = when {
            watchedCount <= 0 -> existingEntry?.status ?: LibraryStatus.PlanToWatch
            totalEpisodes != null && totalEpisodes > 0 && watchedCount >= totalEpisodes -> LibraryStatus.Completed
            else -> LibraryStatus.Watching
        }

        runCatching {
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
        }
    }
}