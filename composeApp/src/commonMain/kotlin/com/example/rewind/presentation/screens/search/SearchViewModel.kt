package com.example.rewind.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rewind.core.network.NetworkResult
import com.example.rewind.data.remote.dto.TmdbMovieDto
import com.example.rewind.domain.model.Movie
import com.example.rewind.domain.model.MovieGenre
import com.example.rewind.domain.model.MovieType
import com.example.rewind.domain.model.WatchStatus
import com.example.rewind.domain.usecase.GetTrendingUseCase
import com.example.rewind.domain.usecase.SaveMovieUseCase
import com.example.rewind.domain.usecase.SearchTmdbUseCase
import com.example.rewind.data.remote.dto.TmdbGenreMapper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

// ==================== UI STATE ====================

sealed interface SearchUiState {
    data object Idle : SearchUiState                        // Belum ada query
    data object Loading : SearchUiState                     // Sedang fetch
    data class Success(
        val results: List<TmdbMovieDto>,
        val query: String
    ) : SearchUiState                                       // Ada hasil
    data object Empty : SearchUiState                       // Tidak ada hasil
    data class Error(val message: String) : SearchUiState   // Gagal fetch
}

sealed interface TrendingUiState {
    data object Loading : TrendingUiState
    data class Success(val items: List<TmdbMovieDto>) : TrendingUiState
    data class Error(val message: String) : TrendingUiState
}

sealed interface AddToCollectionState {
    data object Idle : AddToCollectionState
    data object Loading : AddToCollectionState
    data class Success(val title: String) : AddToCollectionState
    data class Error(val message: String) : AddToCollectionState
}

// ==================== VIEWMODEL ====================

class SearchViewModel(
    private val searchTmdb: SearchTmdbUseCase,
    private val getTrending: GetTrendingUseCase,
    private val saveMovie: SaveMovieUseCase
) : ViewModel() {

    private val _searchState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val searchState: StateFlow<SearchUiState> = _searchState.asStateFlow()

    private val _trendingState = MutableStateFlow<TrendingUiState>(TrendingUiState.Loading)
    val trendingState: StateFlow<TrendingUiState> = _trendingState.asStateFlow()

    private val _addState = MutableStateFlow<AddToCollectionState>(AddToCollectionState.Idle)
    val addState: StateFlow<AddToCollectionState> = _addState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadTrending()
    }

    // ==================== SEARCH ====================

    fun onQueryChange(query: String) {
        searchJob?.cancel()

        if (query.isBlank()) {
            _searchState.value = SearchUiState.Idle
            return
        }

        searchJob = viewModelScope.launch {
            delay(500) // debounce 500ms
            _searchState.value = SearchUiState.Loading

            when (val result = searchTmdb(query)) {
                is NetworkResult.Success -> {
                    _searchState.value = if (result.data.isEmpty()) {
                        SearchUiState.Empty
                    } else {
                        SearchUiState.Success(results = result.data, query = query)
                    }
                }
                is NetworkResult.Error -> {
                    _searchState.value = SearchUiState.Error(result.message)
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }

    fun clearSearch() {
        searchJob?.cancel()
        _searchState.value = SearchUiState.Idle
    }

    // ==================== TRENDING ====================

    fun loadTrending() {
        viewModelScope.launch {
            _trendingState.value = TrendingUiState.Loading
            when (val result = getTrending()) {
                is NetworkResult.Success -> {
                    _trendingState.value = TrendingUiState.Success(result.data)
                }
                is NetworkResult.Error -> {
                    _trendingState.value = TrendingUiState.Error(result.message)
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }

    // ==================== ADD TO COLLECTION ====================

    fun addToCollection(item: TmdbMovieDto) {
        viewModelScope.launch {
            _addState.value = AddToCollectionState.Loading

            val genreString = TmdbGenreMapper.fromGenreIds(item.genreIds)
            val genre = try {
                MovieGenre.valueOf(genreString)
            } catch (e: IllegalArgumentException) {
                MovieGenre.OTHER
            }

            val type = if (item.isTvSeries) MovieType.SERIES else MovieType.MOVIE

            val movie = Movie(
                title = item.displayTitle,
                genre = genre,
                type = type,
                status = WatchStatus.PLAN_TO_WATCH,  // Default: rencana ditonton
                rating = null,
                review = item.overview?.take(200) ?: "",  // Gunakan overview TMDB sebagai review awal
                totalEpisodes = null,
                watchedEpisodes = 0,
                createdAt = Clock.System.now(),
                updatedAt = Clock.System.now()
            )

            val result = saveMovie(movie)
            _addState.value = if (result.isSuccess) {
                AddToCollectionState.Success(item.displayTitle)
            } else {
                AddToCollectionState.Error(result.exceptionOrNull()?.message ?: "Gagal menambahkan")
            }
        }
    }

    fun resetAddState() {
        _addState.value = AddToCollectionState.Idle
    }
}