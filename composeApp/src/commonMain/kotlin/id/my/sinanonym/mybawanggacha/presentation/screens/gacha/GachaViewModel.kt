package id.my.sinanonym.mybawanggacha.presentation.screens.gacha

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.my.sinanonym.mybawanggacha.domain.gacha.model.GachaHistoryEntry
import id.my.sinanonym.mybawanggacha.domain.gacha.model.GachaMediaFormat
import id.my.sinanonym.mybawanggacha.domain.gacha.model.GachaMediaPool
import id.my.sinanonym.mybawanggacha.domain.gacha.model.GachaPreference
import id.my.sinanonym.mybawanggacha.domain.gacha.model.GachaResultItem
import id.my.sinanonym.mybawanggacha.domain.gacha.model.GachaResultMediaType
import id.my.sinanonym.mybawanggacha.domain.gacha.repository.GachaRepository
import id.my.sinanonym.mybawanggacha.domain.gacha.usecase.RunGachaUseCase
import id.my.sinanonym.mybawanggacha.domain.library.model.LibraryEntry
import id.my.sinanonym.mybawanggacha.domain.library.model.LibraryStatus
import id.my.sinanonym.mybawanggacha.domain.library.model.UserProgress
import id.my.sinanonym.mybawanggacha.domain.library.repository.LibraryRepository
import id.my.sinanonym.mybawanggacha.domain.search.model.SearchFilterOption
import id.my.sinanonym.mybawanggacha.domain.search.model.SearchMediaType
import id.my.sinanonym.mybawanggacha.domain.search.repository.SearchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GachaViewModel(
    private val runGachaUseCase: RunGachaUseCase,
    private val gachaRepository: GachaRepository,
    private val libraryRepository: LibraryRepository,
    private val searchRepository: SearchRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(GachaUiState())
    val uiState: StateFlow<GachaUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            gachaRepository.observeLastPreference().collect { preference ->
                val normalized = preference.withValidFormat()
                _uiState.update { state ->
                    state.copy(preference = normalized)
                }
                refreshGenres(normalized.mediaPool)
            }
        }

        viewModelScope.launch {
            gachaRepository.observeHistory().collect { history ->
                _uiState.update { state -> state.copy(history = history) }
            }
        }
    }

    fun updatePreference(transform: (GachaPreference) -> GachaPreference) {
        val previous = _uiState.value.preference
        val updated = transform(previous).withValidFormat()

        _uiState.update { state ->
            state.copy(
                preference = updated,
                errorMessage = null,
                infoMessage = null
            )
        }

        if (updated.mediaPool != previous.mediaPool) {
            refreshGenres(updated.mediaPool)
        }
    }

    fun runGacha() {
        val preference = _uiState.value.preference.withValidFormat()
        _uiState.update { state ->
            state.copy(
                preference = preference,
                isLoading = true,
                errorMessage = null,
                infoMessage = null
            )
        }

        viewModelScope.launch {
            runCatching {
                gachaRepository.saveLastPreference(preference)
                runGachaUseCase(preference)
            }.onSuccess { item ->
                gachaRepository.saveHistoryEntry(GachaHistoryEntry(item = item))
                _uiState.update { state ->
                    state.copy(
                        result = item,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }.onFailure { error ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Gacha gagal dijalankan."
                    )
                }
            }
        }
    }

    fun addResultToLibrary() {
        val item = _uiState.value.result ?: return

        viewModelScope.launch {
            runCatching {
                libraryRepository.upsertEntry(item.toLibraryEntry())
            }.onSuccess {
                _uiState.update { state ->
                    state.copy(
                        infoMessage = "Ditambahkan ke My Library",
                        errorMessage = null
                    )
                }
            }.onFailure { error ->
                _uiState.update { state ->
                    state.copy(
                        errorMessage = error.message ?: "Gagal menambahkan ke My Library"
                    )
                }
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            gachaRepository.clearHistory()
        }
    }

    private fun refreshGenres(mediaPool: GachaMediaPool) {
        _uiState.update { state ->
            state.copy(
                isGenreLoading = true,
                genreErrorMessage = null
            )
        }

        viewModelScope.launch {
            runCatching {
                loadGenreOptions(mediaPool)
            }.onSuccess { genres ->
                _uiState.update { state ->
                    state.copy(
                        preference = state.preference.withValidGenres(genres),
                        availableGenres = genres,
                        isGenreLoading = false,
                        genreErrorMessage = null
                    )
                }
            }.onFailure { error ->
                _uiState.update { state ->
                    state.copy(
                        availableGenres = emptyList(),
                        isGenreLoading = false,
                        genreErrorMessage = error.message ?: "Gagal memuat genre"
                    )
                }
            }
        }
    }

    private suspend fun loadGenreOptions(mediaPool: GachaMediaPool): List<SearchFilterOption> {
        return when (mediaPool) {
            GachaMediaPool.Anime -> {
                searchRepository.getFilterMetadata(SearchMediaType.Anime)
                    .genres
                    .normalizedGenreOptions()
            }
            GachaMediaPool.Manga -> {
                searchRepository.getFilterMetadata(SearchMediaType.Manga)
                    .genres
                    .normalizedGenreOptions()
            }
            GachaMediaPool.Both -> {
                val animeGenres = searchRepository.getFilterMetadata(SearchMediaType.Anime).genres
                val mangaGenres = searchRepository.getFilterMetadata(SearchMediaType.Manga).genres
                (animeGenres + mangaGenres).normalizedGenreOptions()
            }
        }
    }

    private fun GachaPreference.withValidGenres(
        availableGenres: List<SearchFilterOption>
    ): GachaPreference {
        val normalizedIncludedGenres = selectedGenreIds.normalizeGenreIdsByName(availableGenres)
        val normalizedExcludedGenres = excludedGenreIds
            .normalizeGenreIdsByName(availableGenres)
            .filterNot { genreId -> genreId in normalizedIncludedGenres }

        return copy(
            selectedGenreIds = normalizedIncludedGenres,
            excludedGenreIds = normalizedExcludedGenres
        )
    }

    private fun GachaPreference.withValidFormat(): GachaPreference {
        val normalizedIncludedGenres = selectedGenreIds.distinct()
        val normalizedExcludedGenres = excludedGenreIds
            .distinct()
            .filterNot { genreId -> genreId in normalizedIncludedGenres }
        val normalizedFormat = if (format in GachaMediaFormat.availableFor(mediaPool)) {
            format
        } else {
            GachaMediaFormat.Any
        }

        return copy(
            selectedGenreIds = normalizedIncludedGenres,
            excludedGenreIds = normalizedExcludedGenres,
            format = normalizedFormat
        )
    }

    private fun List<SearchFilterOption>.normalizedGenreOptions(): List<SearchFilterOption> {
        return distinctBy { option -> option.normalizedGenreName() }
            .sortedBy { option -> option.name.lowercase() }
    }

    private fun List<Int>.normalizeGenreIdsByName(
        availableGenres: List<SearchFilterOption>
    ): List<Int> {
        val genresById = availableGenres.associateBy { option -> option.id }
        return mapNotNull { genreId -> genresById[genreId] }
            .distinctBy { option -> option.normalizedGenreName() }
            .map { option -> option.id }
    }

    private fun SearchFilterOption.normalizedGenreName(): String {
        return name.trim().lowercase()
    }

    private fun GachaResultItem.toLibraryEntry(): LibraryEntry {
        return LibraryEntry(
            mediaId = malId,
            mediaType = toLibraryMediaType(),
            title = title,
            imageUrl = imageUrl,
            status = LibraryStatus.PlanToWatch,
            progress = UserProgress(
                current = 0,
                total = when (mediaType) {
                    GachaResultMediaType.Anime -> episodes
                    GachaResultMediaType.Manga -> chapters ?: volumes
                }
            ),
            notes = "Added from Gacha"
        )
    }
}
