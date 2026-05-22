package com.example.rewind.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rewind.domain.usecase.DeleteMovieUseCase
import com.example.rewind.domain.usecase.GetMovieByIDUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class DetailViewModel(
    private val getMovieByID: GetMovieByIDUseCase,
    private val deleteMovie: DeleteMovieUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadMovie(id: Long) {
        viewModelScope.launch {
            getMovieByID(id)
                .catch { _uiState.value = DetailUiState.NotFound }
                .collect { movie ->
                    _uiState.value = if (movie != null) {
                        DetailUiState.Success(movie)
                    } else {
                        DetailUiState.NotFound
                    }
                }
        }
    }

    fun deleteMovie(id: Long, onDeleted: () -> Unit) {
        viewModelScope.launch {
            deleteMovie.invoke(id)
            onDeleted()
        }
    }
}