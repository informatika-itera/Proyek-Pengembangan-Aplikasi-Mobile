package com.example.rewind.domain.usecase

import app.cash.turbine.test
import com.example.rewind.data.repository.FakeMovieRepository
import com.example.rewind.domain.model.Movie
import com.example.rewind.domain.model.MovieGenre
import com.example.rewind.domain.model.MovieType
import com.example.rewind.domain.model.WatchStatus
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MovieReadUseCasesTest {

    private lateinit var repository: FakeMovieRepository

    // Use cases under test
    private lateinit var getAllMoviesUseCase: GetAllMoviesUseCase
    private lateinit var getMovieByIDUseCase: GetMovieByIDUseCase
    private lateinit var searchMoviesUseCase: SearchMoviesUseCase
    private lateinit var getMoviesByStatusUseCase: GetMoviesByStatusUseCase
    private lateinit var getFavoriteMoviesUseCase: GetFavoriteMoviesUseCase

    // Timestamps for deterministic sorting
    private val oldest = Instant.fromEpochMilliseconds(1000)
    private val middle = Instant.fromEpochMilliseconds(2000)
    private val newest = Instant.fromEpochMilliseconds(3000)

    @BeforeTest
    fun setup() {
        repository = FakeMovieRepository()
        getAllMoviesUseCase = GetAllMoviesUseCase(repository)
        getMovieByIDUseCase = GetMovieByIDUseCase(repository)
        searchMoviesUseCase = SearchMoviesUseCase(repository)
        getMoviesByStatusUseCase = GetMoviesByStatusUseCase(repository)
        getFavoriteMoviesUseCase = GetFavoriteMoviesUseCase(repository)
    }

    // ==================== HELPER ====================

    private fun createTestMovie(
        title: String = "Test Movie",
        rating: Float? = null,
        status: WatchStatus = WatchStatus.PLAN_TO_WATCH,
        type: MovieType = MovieType.MOVIE,
        genre: MovieGenre = MovieGenre.OTHER,
        createdAt: Instant = middle,
        updatedAt: Instant = middle
    ): Movie = Movie(
        id = 0,
        title = title,
        genre = genre,
        type = type,
        status = status,
        rating = rating,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    // ==================== GetAllMoviesUseCase Tests ====================

    @Test
    fun `GetAllMoviesUseCase - empty list returns empty`() = runTest {
        getAllMoviesUseCase(MovieSortBy.TITLE_ASC).test {
            val result = awaitItem()
            assertTrue(result.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GetAllMoviesUseCase - sort by TITLE_ASC`() = runTest {
        // Arrange
        repository.insertMovie(createTestMovie(title = "Zebra"))
        repository.insertMovie(createTestMovie(title = "Alpha"))
        repository.insertMovie(createTestMovie(title = "Middle"))

        // Act & Assert
        getAllMoviesUseCase(MovieSortBy.TITLE_ASC).test {
            val result = awaitItem()
            assertEquals(3, result.size)
            assertEquals("Alpha", result[0].title)
            assertEquals("Middle", result[1].title)
            assertEquals("Zebra", result[2].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GetAllMoviesUseCase - sort by TITLE_DESC`() = runTest {
        // Arrange
        repository.insertMovie(createTestMovie(title = "Alpha"))
        repository.insertMovie(createTestMovie(title = "Zebra"))
        repository.insertMovie(createTestMovie(title = "Middle"))

        // Act & Assert
        getAllMoviesUseCase(MovieSortBy.TITLE_DESC).test {
            val result = awaitItem()
            assertEquals(3, result.size)
            assertEquals("Zebra", result[0].title)
            assertEquals("Middle", result[1].title)
            assertEquals("Alpha", result[2].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GetAllMoviesUseCase - sort by RATING_DESC`() = runTest {
        // Arrange
        repository.insertMovie(createTestMovie(title = "Low", rating = 3.0f))
        repository.insertMovie(createTestMovie(title = "High", rating = 9.0f))
        repository.insertMovie(createTestMovie(title = "No Rating", rating = null))

        // Act & Assert
        getAllMoviesUseCase(MovieSortBy.RATING_DESC).test {
            val result = awaitItem()
            assertEquals(3, result.size)
            assertEquals("High", result[0].title)
            assertEquals("Low", result[1].title)
            assertEquals("No Rating", result[2].title) // null rating treated as 0
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GetAllMoviesUseCase - sort by RATING_ASC`() = runTest {
        // Arrange
        repository.insertMovie(createTestMovie(title = "High", rating = 9.0f))
        repository.insertMovie(createTestMovie(title = "Low", rating = 3.0f))
        repository.insertMovie(createTestMovie(title = "No Rating", rating = null))

        // Act & Assert
        getAllMoviesUseCase(MovieSortBy.RATING_ASC).test {
            val result = awaitItem()
            assertEquals(3, result.size)
            assertEquals("No Rating", result[0].title) // null rating treated as 0
            assertEquals("Low", result[1].title)
            assertEquals("High", result[2].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GetAllMoviesUseCase - sort by UPDATED_DESC`() = runTest {
        // Arrange
        repository.insertMovie(createTestMovie(title = "Old", updatedAt = oldest))
        repository.insertMovie(createTestMovie(title = "New", updatedAt = newest))
        repository.insertMovie(createTestMovie(title = "Mid", updatedAt = middle))

        // Act & Assert
        getAllMoviesUseCase(MovieSortBy.UPDATED_DESC).test {
            val result = awaitItem()
            assertEquals(3, result.size)
            assertEquals("New", result[0].title)
            assertEquals("Mid", result[1].title)
            assertEquals("Old", result[2].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GetAllMoviesUseCase - sort by UPDATED_ASC`() = runTest {
        // Arrange
        repository.insertMovie(createTestMovie(title = "New", updatedAt = newest))
        repository.insertMovie(createTestMovie(title = "Old", updatedAt = oldest))
        repository.insertMovie(createTestMovie(title = "Mid", updatedAt = middle))

        // Act & Assert
        getAllMoviesUseCase(MovieSortBy.UPDATED_ASC).test {
            val result = awaitItem()
            assertEquals(3, result.size)
            assertEquals("Old", result[0].title)
            assertEquals("Mid", result[1].title)
            assertEquals("New", result[2].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GetAllMoviesUseCase - sort by CREATED_DESC`() = runTest {
        // Arrange
        repository.insertMovie(createTestMovie(title = "Old", createdAt = oldest))
        repository.insertMovie(createTestMovie(title = "New", createdAt = newest))
        repository.insertMovie(createTestMovie(title = "Mid", createdAt = middle))

        // Act & Assert
        getAllMoviesUseCase(MovieSortBy.CREATED_DESC).test {
            val result = awaitItem()
            assertEquals(3, result.size)
            assertEquals("New", result[0].title)
            assertEquals("Mid", result[1].title)
            assertEquals("Old", result[2].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GetAllMoviesUseCase - sort by CREATED_ASC`() = runTest {
        // Arrange
        repository.insertMovie(createTestMovie(title = "New", createdAt = newest))
        repository.insertMovie(createTestMovie(title = "Old", createdAt = oldest))
        repository.insertMovie(createTestMovie(title = "Mid", createdAt = middle))

        // Act & Assert
        getAllMoviesUseCase(MovieSortBy.CREATED_ASC).test {
            val result = awaitItem()
            assertEquals(3, result.size)
            assertEquals("Old", result[0].title)
            assertEquals("Mid", result[1].title)
            assertEquals("New", result[2].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== GetMovieByIDUseCase Tests ====================

    @Test
    fun `GetMovieByIDUseCase - existing movie returns movie`() = runTest {
        // Arrange
        val id = repository.insertMovie(createTestMovie(title = "Find Me"))

        // Act & Assert
        getMovieByIDUseCase(id).test {
            val movie = awaitItem()
            assertEquals("Find Me", movie?.title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GetMovieByIDUseCase - non-existing movie returns null`() = runTest {
        // Act & Assert
        getMovieByIDUseCase(999L).test {
            val movie = awaitItem()
            assertNull(movie)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== SearchMoviesUseCase Tests ====================

    @Test
    fun `SearchMoviesUseCase - blank query returns all movies`() = runTest {
        // Arrange
        repository.insertMovie(createTestMovie(title = "Movie A"))
        repository.insertMovie(createTestMovie(title = "Movie B"))

        // Act & Assert
        searchMoviesUseCase("").test {
            val result = awaitItem()
            assertEquals(2, result.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `SearchMoviesUseCase - with query filters by title`() = runTest {
        // Arrange
        repository.insertMovie(createTestMovie(title = "Inception"))
        repository.insertMovie(createTestMovie(title = "Interstellar"))
        repository.insertMovie(createTestMovie(title = "The Matrix"))

        // Act & Assert
        searchMoviesUseCase("Inter").test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("Interstellar", result[0].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `SearchMoviesUseCase - with status filter`() = runTest {
        // Arrange
        repository.insertMovie(createTestMovie(title = "Movie A", status = WatchStatus.WATCHING))
        repository.insertMovie(createTestMovie(title = "Movie B", status = WatchStatus.COMPLETED))
        repository.insertMovie(createTestMovie(title = "Movie C", status = WatchStatus.WATCHING))

        // Act & Assert
        searchMoviesUseCase("", status = WatchStatus.WATCHING).test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertTrue(result.all { it.status == WatchStatus.WATCHING })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `SearchMoviesUseCase - with type filter`() = runTest {
        // Arrange
        repository.insertMovie(createTestMovie(title = "Film A", type = MovieType.MOVIE))
        repository.insertMovie(createTestMovie(title = "Series A", type = MovieType.SERIES))
        repository.insertMovie(createTestMovie(title = "Anime A", type = MovieType.ANIME))

        // Act & Assert
        searchMoviesUseCase("", type = MovieType.SERIES).test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("Series A", result[0].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `SearchMoviesUseCase - with genre filter`() = runTest {
        // Arrange
        repository.insertMovie(createTestMovie(title = "Action Film", genre = MovieGenre.ACTION))
        repository.insertMovie(createTestMovie(title = "Comedy Film", genre = MovieGenre.COMEDY))

        // Act & Assert
        searchMoviesUseCase("", genre = MovieGenre.ACTION).test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("Action Film", result[0].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `SearchMoviesUseCase - combined query and filters`() = runTest {
        // Arrange
        repository.insertMovie(
            createTestMovie(
                title = "Action Movie A",
                status = WatchStatus.WATCHING,
                type = MovieType.MOVIE,
                genre = MovieGenre.ACTION
            )
        )
        repository.insertMovie(
            createTestMovie(
                title = "Action Series B",
                status = WatchStatus.WATCHING,
                type = MovieType.SERIES,
                genre = MovieGenre.ACTION
            )
        )
        repository.insertMovie(
            createTestMovie(
                title = "Comedy Movie C",
                status = WatchStatus.COMPLETED,
                type = MovieType.MOVIE,
                genre = MovieGenre.COMEDY
            )
        )

        // Act & Assert - query "Action" + status WATCHING + type MOVIE
        searchMoviesUseCase(
            "Action",
            status = WatchStatus.WATCHING,
            type = MovieType.MOVIE,
            genre = MovieGenre.ACTION
        ).test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("Action Movie A", result[0].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== GetMoviesByStatusUseCase Tests ====================

    @Test
    fun `GetMoviesByStatusUseCase - returns filtered list`() = runTest {
        // Arrange
        repository.insertMovie(createTestMovie(title = "Watching A", status = WatchStatus.WATCHING))
        repository.insertMovie(createTestMovie(title = "Completed A", status = WatchStatus.COMPLETED))
        repository.insertMovie(createTestMovie(title = "Watching B", status = WatchStatus.WATCHING))

        // Act & Assert
        getMoviesByStatusUseCase(WatchStatus.WATCHING).test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertTrue(result.all { it.status == WatchStatus.WATCHING })
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== GetFavoriteMoviesUseCase Tests ====================

    @Test
    fun `GetFavoriteMoviesUseCase - returns only favorites`() = runTest {
        // Arrange - isFavorite = rating != null && rating >= 8.0f
        repository.insertMovie(createTestMovie(title = "Favorite", rating = 9.0f))
        repository.insertMovie(createTestMovie(title = "Not Favorite", rating = 5.0f))
        repository.insertMovie(createTestMovie(title = "Also Favorite", rating = 8.0f))
        repository.insertMovie(createTestMovie(title = "No Rating"))

        // Act & Assert
        getFavoriteMoviesUseCase().test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertTrue(result.all { it.isFavorite })
            assertTrue(result.any { it.title == "Favorite" })
            assertTrue(result.any { it.title == "Also Favorite" })
            cancelAndIgnoreRemainingEvents()
        }
    }
}
