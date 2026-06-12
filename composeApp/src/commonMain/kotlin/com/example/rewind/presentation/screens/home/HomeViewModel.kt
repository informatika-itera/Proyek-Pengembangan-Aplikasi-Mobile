package com.example.rewind.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rewind.core.network.NetworkResult
import com.example.rewind.data.remote.dto.TmdbMovieDto
import com.example.rewind.domain.model.Movie
import com.example.rewind.domain.model.MovieGenre
import com.example.rewind.domain.model.MovieType
import com.example.rewind.domain.model.WatchStatus
import com.example.rewind.domain.usecase.DeleteMovieUseCase
import com.example.rewind.domain.usecase.GetAllMoviesUseCase
import com.example.rewind.domain.usecase.MovieSortBy
import com.example.rewind.domain.usecase.SaveMovieUseCase
import com.example.rewind.domain.usecase.SearchTmdbUseCase
import com.example.rewind.domain.usecase.GetTrendingUseCase
import com.example.rewind.domain.usecase.GetTmdbDetailUseCase
import com.example.rewind.data.remote.dto.TmdbGenreMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

// State khusus untuk hasil TMDB
sealed interface TmdbSearchState {
    data object Idle : TmdbSearchState
    data object Loading : TmdbSearchState
    data class Success(val results: List<TmdbMovieDto>) : TmdbSearchState
    data object Empty : TmdbSearchState
    data class Error(val message: String) : TmdbSearchState
}

class HomeViewModel(
    private val getAllMovies: GetAllMoviesUseCase,
    private val deleteMovieUseCase: DeleteMovieUseCase,
    private val searchTmdbUseCase: SearchTmdbUseCase,
    private val saveMovieUseCase: SaveMovieUseCase,
    private val getTrendingUseCase: GetTrendingUseCase,
    private val getTmdbDetailUseCase: GetTmdbDetailUseCase
) : ViewModel() {

    private val _sortBy = MutableStateFlow(MovieSortBy.UPDATED_DESC)
    val searchQuery = MutableStateFlow("")

    // State untuk hasil TMDB
    private val _tmdbState = MutableStateFlow<TmdbSearchState>(TmdbSearchState.Idle)
    val tmdbState: StateFlow<TmdbSearchState> = _tmdbState.asStateFlow()

    // State untuk Trending
    private val _trendingState = MutableStateFlow<TmdbSearchState>(TmdbSearchState.Idle)
    val trendingState: StateFlow<TmdbSearchState> = _trendingState.asStateFlow()

    // State notifikasi add to collection
    private val _addMessage = MutableStateFlow<String?>(null)
    val addMessage: StateFlow<String?> = _addMessage.asStateFlow()

    private var tmdbJob: Job? = null

    init {
        fetchTrending()
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val uiState: StateFlow<HomeUiState> = combine(
        _sortBy.flatMapLatest { sort -> getAllMovies(sort) },
        searchQuery.debounce(300).onStart { emit("") }.distinctUntilChanged()
    ) { movies, query ->
        val filtered = if (query.isBlank()) {
            movies
        } else {
            movies.filter { movie ->
                movie.title.contains(query, ignoreCase = true) ||
                        movie.genre.displayName.contains(query, ignoreCase = true) ||
                        movie.type.displayName.contains(query, ignoreCase = true)
            }
        }

        when {
            filtered.isEmpty() && query.isNotBlank() -> HomeUiState.NoResults(query)
            filtered.isEmpty() -> HomeUiState.Empty
            else -> HomeUiState.Success(filtered, _sortBy.value, query)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState.Loading
    )

    fun fetchTrending() {
        viewModelScope.launch {
            _trendingState.value = TmdbSearchState.Loading
            when (val result = getTrendingUseCase()) {
                is NetworkResult.Success -> {
                    _trendingState.value = TmdbSearchState.Success(result.data)
                }
                is NetworkResult.Error -> {
                    _trendingState.value = TmdbSearchState.Error(result.message)
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
        searchTmdb(query)
    }

    // Cari ke TMDB dengan debounce 600ms
    private fun searchTmdb(query: String) {
        tmdbJob?.cancel()

        if (query.length < 2) {
            _tmdbState.value = TmdbSearchState.Idle
            return
        }

        tmdbJob = viewModelScope.launch {
            delay(600)
            _tmdbState.value = TmdbSearchState.Loading

            when (val result = searchTmdbUseCase(query)) {
                is NetworkResult.Success -> {
                    _tmdbState.value = if (result.data.isEmpty()) {
                        TmdbSearchState.Empty
                    } else {
                        TmdbSearchState.Success(result.data)
                    }
                }
                is NetworkResult.Error -> {
                    _tmdbState.value = TmdbSearchState.Error(result.message)
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }

    fun setSortBy(sortBy: MovieSortBy) {
        _sortBy.value = sortBy
    }

    fun deleteMovie(id: Long) {
        viewModelScope.launch {
            deleteMovieUseCase(id)
        }
    }

    // Tambah film dari TMDB ke koleksi lokal dengan status pilihan & menyimpan poster
    fun addTmdbToCollection(
        item: TmdbMovieDto,
        status: WatchStatus,
        rating: Float? = null,
        userReview: String = ""
    ) {
        viewModelScope.launch {
            val genre = try {
                MovieGenre.valueOf(TmdbGenreMapper.fromGenreIds(item.genreIds))
            } catch (e: IllegalArgumentException) {
                MovieGenre.OTHER
            }

            val type = if (item.isTvSeries) MovieType.SERIES else MovieType.MOVIE

            // Ambil jumlah episode yang lebih akurat dari detail API jika ini series
            var totalEpisodes = if (item.isTvSeries) item.numberOfEpisodes else null
            
            if (item.isTvSeries && totalEpisodes == null) {
                when (val detailResult = getTmdbDetailUseCase(item.id, isTv = true)) {
                    is NetworkResult.Success -> {
                        totalEpisodes = detailResult.data.numberOfEpisodes
                    }
                    else -> {}
                }
            }

            val calculatedWatchedEpisodes = if (status == WatchStatus.COMPLETED) {
                totalEpisodes ?: if (type == MovieType.MOVIE) 1 else 0
            } else {
                0
            }

            val movie = Movie(
                title = item.displayTitle,
                genre = genre,
                type = type,
                status = status,
                rating = rating,
                review = userReview,
                synopsis = item.overview ?: "",
                totalEpisodes = totalEpisodes,
                watchedEpisodes = calculatedWatchedEpisodes,
                createdAt = Clock.System.now(),
                updatedAt = Clock.System.now(),
                posterUrl = item.posterUrl("w500") // Simpan URL poster ke database lokal
            )

            val result = saveMovieUseCase(movie)
            _addMessage.value = if (result.isSuccess) {
                "\"${item.displayTitle}\" ditambahkan ke koleksi!"
            } else {
                "Gagal menambahkan ${item.displayTitle}"
            }
        }
    }

    fun clearAddMessage() {
        _addMessage.value = null
    }
}