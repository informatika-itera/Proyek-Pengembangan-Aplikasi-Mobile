package id.my.sinanonym.mybawanggacha.presentation.screens.anime.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.my.sinanonym.mybawanggacha.domain.anime.model.AnimeDetail
import id.my.sinanonym.mybawanggacha.domain.anime.repository.AnimeRepository
import id.my.sinanonym.mybawanggacha.domain.library.model.LibraryEntry
import id.my.sinanonym.mybawanggacha.domain.library.model.LibraryStatus
import id.my.sinanonym.mybawanggacha.domain.library.model.MediaType
import id.my.sinanonym.mybawanggacha.domain.library.model.UserProgress
import id.my.sinanonym.mybawanggacha.domain.library.repository.LibraryRepository
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
        loadAnimeDetail(
            malId = malId,
            keepCurrentContent = false,
            forceRefresh = false
        )
    }

    fun refreshAnimeDetail(malId: Int) {
        loadAnimeDetail(
            malId = malId,
            keepCurrentContent = true,
            forceRefresh = true
        )
    }

    private fun loadAnimeDetail(
        malId: Int,
        keepCurrentContent: Boolean,
        forceRefresh: Boolean
    ) {
        val currentSuccess = _uiState.value as? AnimeDetailUiState.Success
        val canKeepContent = keepCurrentContent && currentSuccess?.anime?.malId == malId

        if (canKeepContent && currentSuccess?.isRefreshing == true) return

        viewModelScope.launch {
            val staleState = _uiState.value as? AnimeDetailUiState.Success
            val shouldKeepStaleState = keepCurrentContent && staleState?.anime?.malId == malId

            if (shouldKeepStaleState && staleState != null) {
                _uiState.value = staleState.copy(isRefreshing = true)
            } else {
                _uiState.value = AnimeDetailUiState.Loading
            }

            runCatching {
                val detail = animeRepository.getAnimeDetail(
                    malId = malId,
                    forceRefresh = forceRefresh
                )
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
                val latestState = _uiState.value as? AnimeDetailUiState.Success
                if (shouldKeepStaleState && latestState != null) {
                    _uiState.value = latestState.copy(isRefreshing = false)
                } else {
                    _uiState.value = AnimeDetailUiState.Error(
                        error.message ?: "Unknown error occurred"
                    )
                }
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


    fun setEpisodeMarked(
        episodeNumber: Int,
        marked: Boolean
    ) {
        val currentState = _uiState.value as? AnimeDetailUiState.Success ?: return

        viewModelScope.launch {
            val animeId = currentState.anime.malId

            runCatching {
                animeRepository.setEpisodeMarked(
                    animeId = animeId,
                    episodeNumber = episodeNumber,
                    marked = marked
                )
            }.onSuccess {
                val latestState = _uiState.value as? AnimeDetailUiState.Success ?: return@onSuccess
                if (latestState.anime.malId != animeId) return@onSuccess

                val updatedEpisodes = latestState.episodes.map { episode ->
                    if (episode.number == episodeNumber) {
                        episode.copy(marked = marked)
                    } else {
                        episode
                    }
                }

                _uiState.value = latestState.copy(episodes = updatedEpisodes)
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