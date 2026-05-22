package com.example.rewind.data.repository

import com.example.rewind.domain.model.Movie
import com.example.rewind.domain.model.MovieGenre
import com.example.rewind.domain.model.MovieType
import com.example.rewind.domain.model.WatchStatus
import com.example.rewind.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * Fake Repository untuk Testing Fitur Movie (Member A / Member C)
 */
class FakeMovieRepository : MovieRepository {

    private val movies = MutableStateFlow<List<Movie>>(emptyList())
    private var nextId = 1L

    override fun getAllMovies(): Flow<List<Movie>> = movies

    override fun getMovieByID(id: Long): Flow<Movie?> {
        return movies.map { list -> list.find { it.id == id } }
    }

    override fun getMoviesByStatus(status: WatchStatus): Flow<List<Movie>> {
        return movies.map { list -> list.filter { it.status == status } }
    }

    override fun getMoviesByType(type: MovieType): Flow<List<Movie>> {
        return movies.map { list -> list.filter { it.type == type } }
    }

    override fun getMoviesByGenre(genre: MovieGenre): Flow<List<Movie>> {
        return movies.map { list -> list.filter { it.genre == genre } }
    }

    override fun searchMovies(query: String): Flow<List<Movie>> {
        return movies.map { list ->
            list.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.review.contains(query, ignoreCase = true)
            }
        }
    }

    override fun getFavoriteMovies(): Flow<List<Movie>> {
        return movies.map { list -> list.filter { it.isFavorite } }
    }

    override suspend fun insertMovie(movie: Movie): Long {
        val id = nextId++
        val newMovie = movie.copy(id = id)
        movies.update { it + newMovie }
        return id
    }

    override suspend fun updateMovie(movie: Movie) {
        movies.update { list ->
            list.map { if (it.id == movie.id) movie else it }
        }
    }

    override suspend fun deleteMovie(id: Long) {
        movies.update { list -> list.filter { it.id != id } }
    }
}