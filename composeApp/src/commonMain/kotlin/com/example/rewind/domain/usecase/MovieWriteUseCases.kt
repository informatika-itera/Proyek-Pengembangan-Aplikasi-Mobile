package com.example.rewind.domain.usecase

import com.example.rewind.domain.model.Movie
import com.example.rewind.domain.model.WatchStatus
import com.example.rewind.domain.repository.MovieRepository

class SaveMovieUseCase(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(movie: Movie): Result<Long> {
        return try {
            if (movie.title.isBlank()) {
                return Result.failure(IllegalArgumentException("Judul film tidak boleh kosong"))
            }

            if (movie.rating != null && (movie.rating < 0f || movie.rating > 10f)) {
                return Result.failure(IllegalArgumentException("Rating harus antara 0 sampai 10"))
            }

            if (movie.totalEpisodes != null && movie.totalEpisodes < 0) {
                return Result.failure(IllegalArgumentException("Jumlah episode tidak boleh negatif"))
            }

            if (movie.watchedEpisodes < 0) {
                return Result.failure(IllegalArgumentException("Episode ditonton tidak boleh negatif"))
            }

            val id = if (movie.id == 0L) {
                repository.insertMovie(movie)
            } else {
                repository.updateMovie(movie)
                movie.id
            }

            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class DeleteMovieUseCase(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(id: Long): Result<Unit> {
        return try {
            repository.deleteMovie(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class UpdateWatchStatusUseCase(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(movie: Movie, newStatus: WatchStatus): Result<Unit> {
        return try {
            val updatedMovie = movie.copy(status = newStatus)
            repository.updateMovie(updatedMovie)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class AddReviewUseCase(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(movie: Movie, review: String, rating: Float?): Result<Unit> {
        return try {
            if (rating != null && (rating < 0f || rating > 10f)) {
                return Result.failure(IllegalArgumentException("Rating harus antara 0 sampai 10"))
            }

            val updatedMovie = movie.copy(review = review, rating = rating)
            repository.updateMovie(updatedMovie)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}