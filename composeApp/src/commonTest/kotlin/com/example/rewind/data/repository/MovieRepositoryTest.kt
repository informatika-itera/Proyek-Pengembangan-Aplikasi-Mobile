package com.example.rewind.data.repository

import app.cash.turbine.test
import com.example.rewind.domain.model.Movie
import com.example.rewind.domain.model.MovieGenre
import com.example.rewind.domain.model.MovieType
import com.example.rewind.domain.model.WatchStatus
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MovieRepositoryTest {

    private lateinit var repository: FakeMovieRepository

    @BeforeTest
    fun setup() {
        repository = FakeMovieRepository()
    }

    @Test
    fun `insertMovie should return new movie id`() = runTest {
        val movie = createTestMovie(title = "Test Movie")
        val id = repository.insertMovie(movie)
        assertTrue(id > 0)
    }

    @Test
    fun `getAllMovies should return all movies`() = runTest {
        repository.insertMovie(createTestMovie(title = "Movie 1"))
        repository.insertMovie(createTestMovie(title = "Movie 2"))

        repository.getAllMovies().test {
            val movies = awaitItem()
            assertEquals(2, movies.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getMovieByID should return correct movie`() = runTest {
        val id = repository.insertMovie(createTestMovie(title = "Find Me"))

        repository.getMovieByID(id).test {
            val movie = awaitItem()
            assertNotNull(movie)
            assertEquals("Find Me", movie.title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteMovie should remove movie from list`() = runTest {
        val id = repository.insertMovie(createTestMovie(title = "To Delete"))
        repository.deleteMovie(id)

        repository.getAllMovies().test {
            val movies = awaitItem()
            assertTrue(movies.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateMovie should change movie data`() = runTest {
        val id = repository.insertMovie(createTestMovie(title = "Original Title"))
        val updatedMovie = createTestMovie(id = id, title = "Updated Title")
        repository.updateMovie(updatedMovie)

        repository.getMovieByID(id).test {
            val movie = awaitItem()
            assertNotNull(movie)
            assertEquals("Updated Title", movie.title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `searchMovies should return matching results`() = runTest {
        repository.insertMovie(createTestMovie(title = "Avengers Endgame"))
        repository.insertMovie(createTestMovie(title = "Spider-Man"))

        repository.searchMovies("Avengers").test {
            val results = awaitItem()
            assertEquals(1, results.size)
            assertEquals("Avengers Endgame", results.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }
    @Test
    fun `getMoviesByStatus should return only matching status`() = runTest {
        repository.insertMovie(createTestMovie(title = "Selesai", status = WatchStatus.COMPLETED))
        repository.insertMovie(createTestMovie(title = "Rencana", status = WatchStatus.PLAN_TO_WATCH))

        repository.getMoviesByStatus(WatchStatus.COMPLETED).test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("Selesai", result.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getMoviesByGenre should return only matching genre`() = runTest {
        repository.insertMovie(createTestMovie(title = "Film Drama", genre = MovieGenre.DRAMA))
        repository.insertMovie(createTestMovie(title = "Film Aksi", genre = MovieGenre.ACTION))

        repository.getMoviesByGenre(MovieGenre.DRAMA).test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("Film Drama", result.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getMoviesByType should return only matching type`() = runTest {
        repository.insertMovie(createTestMovie(title = "Film Biasa", type = MovieType.MOVIE))
        repository.insertMovie(createTestMovie(title = "Serial TV", type = MovieType.SERIES))

        repository.getMoviesByType(MovieType.SERIES).test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("Serial TV", result.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getFavoriteMovies should return movies with rating 8 or above`() = runTest {
        repository.insertMovie(createTestMovie(title = "Favorit", rating = 9.0f))
        repository.insertMovie(createTestMovie(title = "Biasa", rating = 5.0f))
        repository.insertMovie(createTestMovie(title = "Belum Dirating", rating = null))

        repository.getFavoriteMovies().test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("Favorit", result.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `searchMovies should return empty when no match`() = runTest {
        repository.insertMovie(createTestMovie(title = "Interstellar"))

        repository.searchMovies("Avengers").test {
            val result = awaitItem()
            assertTrue(result.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `searchMovies should be case insensitive`() = runTest {
        repository.insertMovie(createTestMovie(title = "Interstellar"))

        repository.searchMovies("interstellar").test {
            val result = awaitItem()
            assertEquals(1, result.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getMovieByID should return null for non-existing id`() = runTest {
        repository.getMovieByID(999L).test {
            val result = awaitItem()
            assertTrue(result == null)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateMovie should not affect other movies`() = runTest {
        val id1 = repository.insertMovie(createTestMovie(title = "Film A"))
        val id2 = repository.insertMovie(createTestMovie(title = "Film B"))

        repository.updateMovie(createTestMovie(id = id1, title = "Film A Updated"))

        repository.getMovieByID(id2).test {
            val movie = awaitItem()
            assertEquals("Film B", movie?.title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteMovie should not affect other movies`() = runTest {
        val id1 = repository.insertMovie(createTestMovie(title = "Tetap Ada"))
        val id2 = repository.insertMovie(createTestMovie(title = "Dihapus"))

        repository.deleteMovie(id2)

        repository.getAllMovies().test {
            val movies = awaitItem()
            assertEquals(1, movies.size)
            assertEquals("Tetap Ada", movies.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getAllMovies should return empty list initially`() = runTest {
        repository.getAllMovies().test {
            val movies = awaitItem()
            assertTrue(movies.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createTestMovie(
        id: Long = 0,
        title: String = "Test Movie",
        genre: MovieGenre = MovieGenre.OTHER,
        type: MovieType = MovieType.MOVIE,
        status: WatchStatus = WatchStatus.PLAN_TO_WATCH,
        rating: Float? = null,
        review: String = "",
        totalEpisodes: Int? = null,
        watchedEpisodes: Int = 0
    ): Movie {
        return Movie(
            id = id,
            title = title,
            genre = genre,
            type = type,
            status = status,
            rating = rating,
            review = review,
            totalEpisodes = totalEpisodes,
            watchedEpisodes = watchedEpisodes,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
    }
}