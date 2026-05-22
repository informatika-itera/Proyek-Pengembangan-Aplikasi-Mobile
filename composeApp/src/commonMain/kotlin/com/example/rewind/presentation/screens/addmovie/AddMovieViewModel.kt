package com.example.rewind.presentation.screens.addmovie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rewind.domain.model.Movie
import com.example.rewind.domain.model.MovieGenre
import com.example.rewind.domain.model.MovieType
import com.example.rewind.domain.model.WatchStatus
import com.example.rewind.domain.usecase.GetMovieByIDUseCase
import com.example.rewind.domain.usecase.SaveMovieUseCase
import com.example.rewind.domain.usecase.UpdateMovieUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class AddMovieViewModel(
    private val saveMovie: SaveMovieUseCase,
    private val updateMovie: UpdateMovieUseCase,
    private val getMovieByID: GetMovieByIDUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddMovieUiState>(AddMovieUiState.Idle)
    val uiState: StateFlow<AddMovieUiState> = _uiState.asStateFlow()

    // Simpan data movie asli saat edit mode
    private var existingMovie: Movie? = null

    // Load data movie kalau edit mode (movieId != null)
    fun loadMovieForEdit(movieId: Long?) {
        if (movieId == null) return
        viewModelScope.launch {
            val movie = getMovieByID(movieId).firstOrNull()
            if (movie != null) {
                existingMovie = movie
                _uiState.value = AddMovieUiState.EditMode(movie)
            }
        }
    }

    fun saveMovie(
        title: String,
        genre: MovieGenre,
        type: MovieType,
        status: WatchStatus,
        rating: Float?,
        review: String,
        totalEpisodes: Int?,
        watchedEpisodes: Int = 0
    ) {
        if (title.isBlank()) {
            _uiState.value = AddMovieUiState.Error("Judul tidak boleh kosong")
            return
        }
        viewModelScope.launch {
            _uiState.value = AddMovieUiState.Loading
            try {
                val existing = existingMovie
                if (existing != null) {
                    // Edit mode — update dengan id yang sama
                    updateMovie(
                        existing.copy(
                            title = title.trim(),
                            genre = genre,
                            type = type,
                            status = status,
                            rating = rating,
                            review = review.trim(),
                            totalEpisodes = totalEpisodes,
                            watchedEpisodes = watchedEpisodes
                        )
                    )
                } else {
                    // Add mode — insert baru
                    saveMovie.invoke(
                        Movie(
                            title = title.trim(),
                            genre = genre,
                            type = type,
                            status = status,
                            rating = rating,
                            review = review.trim(),
                            totalEpisodes = totalEpisodes,
                            watchedEpisodes = 0
                        )
                    )
                }
                _uiState.value = AddMovieUiState.Success
            } catch (e: Exception) {
                _uiState.value = AddMovieUiState.Error(e.message ?: "Gagal menyimpan")
            }
        }
    }

    fun resetState() {
        _uiState.value = AddMovieUiState.Idle
    }
}