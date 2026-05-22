package com.example.rewind.domain.usecase

import com.example.rewind.domain.model.Movie
import com.example.rewind.domain.repository.MovieRepository

class UpdateMovieUseCase(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(movie: Movie) {
        repository.updateMovie(movie)
    }
}