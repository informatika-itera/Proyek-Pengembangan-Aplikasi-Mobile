package com.example.rewind.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rewind.domain.usecase.DeleteMovieUseCase
import com.example.rewind.domain.usecase.GetAllMoviesUseCase
import com.example.rewind.domain.usecase.MovieSortBy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getAllMovies: GetAllMoviesUseCase,
    private val deleteMovie: DeleteMovieUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var currentSort = MovieSortBy.UPDATED_DESC

    init {
        loadMovies()
    }

    private fun loadMovies() {
        viewModelScope.launch {
            getAllMovies(currentSort)
                .catch { e ->
                    _uiState.value = HomeUiState.Error(e.message ?: "Terjadi kesalahan")
                }
                .collect { movies ->
                    _uiState.value = if (movies.isEmpty()) {
                        HomeUiState.Empty
                    } else {
                        HomeUiState.Success(movies, currentSort)
                    }
                }
        }
    }

    fun deleteMovie(id: Long) {
        viewModelScope.launch {
            deleteMovie.invoke(id)
        }
    }

    fun setSortBy(sortBy: MovieSortBy) {
        currentSort = sortBy
        loadMovies()
    }
}