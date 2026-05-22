package com.example.rewind.domain.usecase

import com.example.rewind.domain.model.Movie
import com.example.rewind.domain.model.MovieGenre
import com.example.rewind.domain.model.MovieType
import com.example.rewind.domain.model.WatchStatus
import com.example.rewind.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllMoviesUseCase(
    private val repository: MovieRepository
) {
    operator fun invoke(sortBy: MovieSortBy = MovieSortBy.UPDATED_DESC): Flow<List<Movie>> {
        return repository.getAllMovies().map { movies ->
            sortMovies(movies, sortBy)
        }
    }
    
    private fun sortMovies(movies: List<Movie>, sortBy: MovieSortBy): List<Movie> {
        return when (sortBy) {
            MovieSortBy.TITLE_ASC -> movies.sortedBy { it.title.lowercase() }
            MovieSortBy.TITLE_DESC -> movies.sortedByDescending { it.title.lowercase() }
            MovieSortBy.RATING_DESC -> movies.sortedByDescending { it.rating ?: 0f }
            MovieSortBy.RATING_ASC -> movies.sortedBy { it.rating ?: 0f }
            MovieSortBy.UPDATED_DESC -> movies.sortedByDescending { it.updatedAt }
            MovieSortBy.UPDATED_ASC -> movies.sortedBy { it.updatedAt }
            MovieSortBy.CREATED_DESC -> movies.sortedByDescending { it.createdAt }
            MovieSortBy.CREATED_ASC -> movies.sortedBy { it.createdAt }
        }
    }
}

enum class MovieSortBy(val displayName: String) {
    TITLE_ASC("Judul (A-Z)"),
    TITLE_DESC("Judul (Z-A)"),
    CREATED_ASC("Paling Lama Ditambahkan"),
    CREATED_DESC("Terbaru Ditambahkan"),
    UPDATED_ASC("Paling Lama Diupdate"),
    UPDATED_DESC("Terakhir Diupdate"),
    RATING_DESC("Rating (Tertinggi)"),
    RATING_ASC("Rating (Terendah)")

}

class GetMovieByIDUseCase(
    private val repository: MovieRepository
) {
    operator fun invoke(id: Long): Flow<Movie?> {
        return repository.getMovieByID(id)
    }
}

class SearchMoviesUseCase(
    private val repository: MovieRepository
) {
    operator fun invoke(
        query: String,
        status: WatchStatus? = null,
        type: MovieType? = null,
        genre: MovieGenre? = null
    ): Flow<List<Movie>> {
        val baseFlow = if (query.isBlank()) {
            repository.getAllMovies()
        } else {
            repository.searchMovies(query)
        }

        return baseFlow.map { movies ->
            movies.filter { movie ->
                (status == null || movie.status == status) &&
                        (type == null || movie.type == type) &&
                        (genre == null || movie.genre == genre)
            }
        }
    }
}

class GetMoviesByStatusUseCase(
    private val repository: MovieRepository
) {
    operator fun invoke(status: WatchStatus): Flow<List<Movie>> {
        return repository.getMoviesByStatus(status)
    }
}

class GetFavoriteMoviesUseCase(
    private val repository: MovieRepository
) {
    operator fun invoke(): Flow<List<Movie>> {
        return repository.getFavoriteMovies()
    }
}