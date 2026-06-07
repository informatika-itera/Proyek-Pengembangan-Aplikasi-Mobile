package com.example.rewind.domain.usecase

import app.cash.turbine.test
import com.example.rewind.data.repository.FakeMovieRepository
import com.example.rewind.domain.model.Movie
import com.example.rewind.domain.model.WatchStatus
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MovieWriteUseCasesTest {

    private lateinit var repository: FakeMovieRepository

    // Use cases under test
    private lateinit var saveMovieUseCase: SaveMovieUseCase
    private lateinit var deleteMovieUseCase: DeleteMovieUseCase
    private lateinit var updateWatchStatusUseCase: UpdateWatchStatusUseCase
    private lateinit var addReviewUseCase: AddReviewUseCase
    private lateinit var updateMovieUseCase: UpdateMovieUseCase

    @BeforeTest
    fun setup() {
        repository = FakeMovieRepository()
        saveMovieUseCase = SaveMovieUseCase(repository)
        deleteMovieUseCase = DeleteMovieUseCase(repository)
        updateWatchStatusUseCase = UpdateWatchStatusUseCase(repository)
        addReviewUseCase = AddReviewUseCase(repository)
        updateMovieUseCase = UpdateMovieUseCase(repository)
    }

    // ==================== HELPER ====================

    private fun createTestMovie(
        id: Long = 0,
        title: String = "Test Movie",
        rating: Float? = null,
        totalEpisodes: Int? = null,
        watchedEpisodes: Int = 0,
        status: WatchStatus = WatchStatus.PLAN_TO_WATCH,
        review: String = ""
    ): Movie = Movie(
        id = id,
        title = title,
        rating = rating,
        totalEpisodes = totalEpisodes,
        watchedEpisodes = watchedEpisodes,
        status = status,
        review = review,
        createdAt = Clock.System.now(),
        updatedAt = Clock.System.now()
    )

    // ==================== SaveMovieUseCase Tests ====================

    @Test
    fun `SaveMovieUseCase - blank title fails`() = runTest {
        // Arrange
        val movie = createTestMovie(title = "   ")

        // Act
        val result = saveMovieUseCase(movie)

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertTrue(result.exceptionOrNull()!!.message!!.contains("Judul film tidak boleh kosong"))
    }

    @Test
    fun `SaveMovieUseCase - rating below 0 fails`() = runTest {
        // Arrange
        val movie = createTestMovie(rating = -1.0f)

        // Act
        val result = saveMovieUseCase(movie)

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertTrue(result.exceptionOrNull()!!.message!!.contains("Rating harus antara 0 sampai 10"))
    }

    @Test
    fun `SaveMovieUseCase - rating above 10 fails`() = runTest {
        // Arrange
        val movie = createTestMovie(rating = 11.0f)

        // Act
        val result = saveMovieUseCase(movie)

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertTrue(result.exceptionOrNull()!!.message!!.contains("Rating harus antara 0 sampai 10"))
    }

    @Test
    fun `SaveMovieUseCase - negative totalEpisodes fails`() = runTest {
        // Arrange
        val movie = createTestMovie(totalEpisodes = -5)

        // Act
        val result = saveMovieUseCase(movie)

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertTrue(result.exceptionOrNull()!!.message!!.contains("Jumlah episode tidak boleh negatif"))
    }

    @Test
    fun `SaveMovieUseCase - negative watchedEpisodes fails`() = runTest {
        // Arrange
        val movie = createTestMovie(watchedEpisodes = -1)

        // Act
        val result = saveMovieUseCase(movie)

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertTrue(result.exceptionOrNull()!!.message!!.contains("Episode ditonton tidak boleh negatif"))
    }

    @Test
    fun `SaveMovieUseCase - new movie with id 0 inserts and returns new id`() = runTest {
        // Arrange
        val movie = createTestMovie(id = 0, title = "New Movie")

        // Act
        val result = saveMovieUseCase(movie)

        // Assert
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!! > 0)

        // Verify it was actually inserted
        repository.getAllMovies().test {
            val movies = awaitItem()
            assertEquals(1, movies.size)
            assertEquals("New Movie", movies[0].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `SaveMovieUseCase - existing movie with id greater than 0 updates`() = runTest {
        // Arrange - first insert a movie
        val insertedId = repository.insertMovie(createTestMovie(title = "Original"))
        val updatedMovie = createTestMovie(id = insertedId, title = "Updated")

        // Act
        val result = saveMovieUseCase(updatedMovie)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(insertedId, result.getOrNull())

        // Verify it was updated
        repository.getMovieByID(insertedId).test {
            val movie = awaitItem()
            assertEquals("Updated", movie?.title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `SaveMovieUseCase - valid rating in range succeeds`() = runTest {
        // Arrange
        val movie = createTestMovie(title = "Rated Movie", rating = 7.5f)

        // Act
        val result = saveMovieUseCase(movie)

        // Assert
        assertTrue(result.isSuccess)
    }

    // ==================== DeleteMovieUseCase Tests ====================

    @Test
    fun `DeleteMovieUseCase - success`() = runTest {
        // Arrange
        val id = repository.insertMovie(createTestMovie(title = "To Delete"))

        // Act
        val result = deleteMovieUseCase(id)

        // Assert
        assertTrue(result.isSuccess)

        // Verify deletion
        repository.getAllMovies().test {
            val movies = awaitItem()
            assertTrue(movies.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== UpdateWatchStatusUseCase Tests ====================

    @Test
    fun `UpdateWatchStatusUseCase - updates status successfully`() = runTest {
        // Arrange
        val id = repository.insertMovie(createTestMovie(title = "Watch Me", status = WatchStatus.PLAN_TO_WATCH))
        val movie = createTestMovie(id = id, title = "Watch Me", status = WatchStatus.PLAN_TO_WATCH)

        // Act
        val result = updateWatchStatusUseCase(movie, WatchStatus.WATCHING)

        // Assert
        assertTrue(result.isSuccess)

        // Verify the status was updated
        repository.getMovieByID(id).test {
            val updatedMovie = awaitItem()
            assertEquals(WatchStatus.WATCHING, updatedMovie?.status)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== AddReviewUseCase Tests ====================

    @Test
    fun `AddReviewUseCase - valid review succeeds`() = runTest {
        // Arrange
        val id = repository.insertMovie(createTestMovie(title = "Review Me"))
        val movie = createTestMovie(id = id, title = "Review Me")

        // Act
        val result = addReviewUseCase(movie, "Great movie!", 8.5f)

        // Assert
        assertTrue(result.isSuccess)

        // Verify review and rating were applied
        repository.getMovieByID(id).test {
            val updatedMovie = awaitItem()
            assertEquals("Great movie!", updatedMovie?.review)
            assertEquals(8.5f, updatedMovie?.rating)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `AddReviewUseCase - invalid rating fails`() = runTest {
        // Arrange
        val movie = createTestMovie(id = 1, title = "Review Me")

        // Act
        val result = addReviewUseCase(movie, "Some review", 15.0f)

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertTrue(result.exceptionOrNull()!!.message!!.contains("Rating harus antara 0 sampai 10"))
    }

    @Test
    fun `AddReviewUseCase - null rating succeeds`() = runTest {
        // Arrange
        val id = repository.insertMovie(createTestMovie(title = "No Rating Review"))
        val movie = createTestMovie(id = id, title = "No Rating Review")

        // Act
        val result = addReviewUseCase(movie, "Decent movie", null)

        // Assert
        assertTrue(result.isSuccess)

        // Verify review was applied with null rating
        repository.getMovieByID(id).test {
            val updatedMovie = awaitItem()
            assertEquals("Decent movie", updatedMovie?.review)
            assertEquals(null, updatedMovie?.rating)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== UpdateMovieUseCase Tests ====================

    @Test
    fun `UpdateMovieUseCase - delegates to repository`() = runTest {
        // Arrange
        val id = repository.insertMovie(createTestMovie(title = "Original Title"))
        val updatedMovie = createTestMovie(id = id, title = "Updated Title", rating = 7.0f)

        // Act
        updateMovieUseCase(updatedMovie)

        // Assert - verify the repository was called
        repository.getMovieByID(id).test {
            val movie = awaitItem()
            assertEquals("Updated Title", movie?.title)
            assertEquals(7.0f, movie?.rating)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
