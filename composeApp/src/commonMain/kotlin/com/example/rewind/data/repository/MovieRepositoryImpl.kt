package com.example.rewind.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.rewind.data.local.RewindDatabase
import com.example.rewind.data.local.entity.toMovie
import com.example.rewind.domain.model.Movie
import com.example.rewind.domain.model.MovieGenre
import com.example.rewind.domain.model.MovieType
import com.example.rewind.domain.model.WatchStatus
import com.example.rewind.domain.repository.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MovieRepositoryImpl(
    database: RewindDatabase
) : MovieRepository {

    private val queries = database.movieQueries

    override fun getAllMovies(): Flow<List<Movie>> = queries.selectAllMovies()
        .asFlow().mapToList(Dispatchers.IO)
        .map { list -> list.map { it.toMovie() } }

    override fun getMovieByID(id: Long): Flow<Movie?> = queries.getMovieByID(id)
        .asFlow().mapToOneOrNull(Dispatchers.IO)
        .map { it?.toMovie() }

    override fun getMoviesByStatus(status: WatchStatus): Flow<List<Movie>> = queries.getMoviesByStatus(status.name)
        .asFlow().mapToList(Dispatchers.IO)
        .map { list -> list.map { it.toMovie() } }

    override fun getMoviesByType(type: MovieType): Flow<List<Movie>> = queries.getMoviesByType(type.name)
        .asFlow().mapToList(Dispatchers.IO)
        .map { list -> list.map { it.toMovie() } }

    override fun getMoviesByGenre(genre: MovieGenre): Flow<List<Movie>> = queries.getMoviesByGenre(genre.name)
        .asFlow().mapToList(Dispatchers.IO)
        .map { list -> list.map { it.toMovie() } }

    override fun searchMovies(query: String): Flow<List<Movie>> = queries.searchMovies(query)
        .asFlow().mapToList(Dispatchers.IO)
        .map { list -> list.map { it.toMovie() } }

    override fun getFavoriteMovies(): Flow<List<Movie>> = queries.getFavoriteMovies()
        .asFlow().mapToList(Dispatchers.IO)
        .map { list -> list.map { it.toMovie() } }

    override suspend fun insertMovie(movie: Movie): Long {
        // Masukkan data ke tabel
        queries.insertMovie(
            movie.title, movie.genre.name, movie.type.name, movie.status.name,
            movie.rating?.toDouble(), movie.review, movie.totalEpisodes?.toLong(),
            movie.watchedEpisodes.toLong(), movie.createdAt.toEpochMilliseconds(),
            movie.updatedAt.toEpochMilliseconds()
        )
        // Kembalikan ID yang baru saja dibuat
        return queries.lastInsertRowId().executeAsOne()
    }

    override suspend fun updateMovie(movie: Movie) {
        queries.updateMovie(
            movie.title, movie.genre.name, movie.type.name, movie.status.name,
            movie.rating?.toDouble(), movie.review, movie.totalEpisodes?.toLong(),
            movie.watchedEpisodes.toLong(), movie.updatedAt.toEpochMilliseconds(),
            movie.id
        )
    }

    override suspend fun deleteMovie(id: Long) {
        queries.deleteMovieById(id)
    }
}