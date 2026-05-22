package com.example.rewind.domain.repository

import com.example.rewind.domain.model.Movie
import com.example.rewind.domain.model.MovieGenre
import com.example.rewind.domain.model.MovieType
import com.example.rewind.domain.model.WatchStatus
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getAllMovies(): Flow<List<Movie>>
    fun getMovieByID(id: Long): Flow<Movie?>
    fun getMoviesByStatus(status: WatchStatus): Flow<List<Movie>>
    fun getMoviesByType(type: MovieType): Flow<List<Movie>>
    fun getMoviesByGenre(genre: MovieGenre): Flow<List<Movie>>
    fun searchMovies(query: String): Flow<List<Movie>>
    fun getFavoriteMovies(): Flow<List<Movie>>
    suspend fun insertMovie(movie: Movie): Long
    suspend fun updateMovie(movie: Movie)
    suspend fun deleteMovie(id: Long)
}