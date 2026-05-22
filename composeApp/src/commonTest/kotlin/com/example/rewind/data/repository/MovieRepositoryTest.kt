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