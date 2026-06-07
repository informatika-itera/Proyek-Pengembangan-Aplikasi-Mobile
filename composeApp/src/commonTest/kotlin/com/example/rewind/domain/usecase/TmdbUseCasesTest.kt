package com.example.rewind.domain.usecase

import com.example.rewind.core.network.NetworkResult
import com.example.rewind.data.remote.dto.TmdbMovieDetailDto
import com.example.rewind.data.remote.dto.TmdbMovieDto
import com.example.rewind.data.repository.FakeTmdbRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TmdbUseCasesTest {

    private lateinit var repository: FakeTmdbRepository

    // Use cases under test
    private lateinit var searchTmdbUseCase: SearchTmdbUseCase
    private lateinit var getTrendingUseCase: GetTrendingUseCase
    private lateinit var getTmdbDetailUseCase: GetTmdbDetailUseCase

    @BeforeTest
    fun setup() {
        repository = FakeTmdbRepository()
        searchTmdbUseCase = SearchTmdbUseCase(repository)
        getTrendingUseCase = GetTrendingUseCase(repository)
        getTmdbDetailUseCase = GetTmdbDetailUseCase(repository)
    }

    // ==================== SearchTmdbUseCase Tests ====================

    @Test
    fun `SearchTmdbUseCase - query less than 2 chars returns empty list`() = runTest {
        // Arrange - set a non-empty result to verify it's NOT called
        repository.searchResult = NetworkResult.Success(
            listOf(TmdbMovieDto(id = 1, title = "Should Not Appear"))
        )

        // Act
        val result = searchTmdbUseCase("a")

        // Assert
        assertIs<NetworkResult.Success<List<TmdbMovieDto>>>(result)
        assertTrue(result.data.isEmpty())
        // The repository should not have been called
        assertNull(repository.lastSearchQuery)
    }

    @Test
    fun `SearchTmdbUseCase - single char returns empty list`() = runTest {
        // Act
        val result = searchTmdbUseCase("x")

        // Assert
        assertIs<NetworkResult.Success<List<TmdbMovieDto>>>(result)
        assertTrue(result.data.isEmpty())
        assertNull(repository.lastSearchQuery)
    }

    @Test
    fun `SearchTmdbUseCase - valid query delegates to repository`() = runTest {
        // Arrange
        val expected = listOf(
            TmdbMovieDto(id = 1, title = "Inception"),
            TmdbMovieDto(id = 2, title = "Interstellar")
        )
        repository.searchResult = NetworkResult.Success(expected)

        // Act
        val result = searchTmdbUseCase("Inception")

        // Assert
        assertIs<NetworkResult.Success<List<TmdbMovieDto>>>(result)
        assertEquals(2, result.data.size)
        assertEquals("Inception", result.data[0].title)
        assertEquals("Inception", repository.lastSearchQuery)
        assertEquals(1, repository.lastSearchPage)
    }

    @Test
    fun `SearchTmdbUseCase - trims whitespace from query`() = runTest {
        // Arrange
        repository.searchResult = NetworkResult.Success(
            listOf(TmdbMovieDto(id = 1, title = "Matrix"))
        )

        // Act
        val result = searchTmdbUseCase("  Matrix  ")

        // Assert
        assertIs<NetworkResult.Success<List<TmdbMovieDto>>>(result)
        assertEquals("Matrix", repository.lastSearchQuery)
    }

    // ==================== GetTrendingUseCase Tests ====================

    @Test
    fun `GetTrendingUseCase - delegates to repository`() = runTest {
        // Arrange
        val expected = listOf(
            TmdbMovieDto(id = 1, title = "Trending 1"),
            TmdbMovieDto(id = 2, title = "Trending 2")
        )
        repository.trendingResult = NetworkResult.Success(expected)

        // Act
        val result = getTrendingUseCase()

        // Assert
        assertIs<NetworkResult.Success<List<TmdbMovieDto>>>(result)
        assertEquals(2, result.data.size)
        assertEquals("Trending 1", result.data[0].title)
    }

    @Test
    fun `GetTrendingUseCase - returns expected error result`() = runTest {
        // Arrange
        repository.trendingResult = NetworkResult.Error("Network error", 500)

        // Act
        val result = getTrendingUseCase()

        // Assert
        assertIs<NetworkResult.Error>(result)
        assertEquals("Network error", result.message)
        assertEquals(500, result.code)
    }

    // ==================== GetTmdbDetailUseCase Tests ====================

    @Test
    fun `GetTmdbDetailUseCase - isTv false calls getMovieDetail`() = runTest {
        // Arrange
        val expectedDetail = TmdbMovieDetailDto(
            id = 42,
            title = "The Matrix",
            overview = "A movie about the matrix"
        )
        repository.movieDetailResult = NetworkResult.Success(expectedDetail)

        // Act
        val result = getTmdbDetailUseCase(tmdbId = 42, isTv = false)

        // Assert
        assertIs<NetworkResult.Success<TmdbMovieDetailDto>>(result)
        assertEquals(42, result.data.id)
        assertEquals("The Matrix", result.data.title)
        assertEquals(42, repository.lastMovieDetailId)
        assertNull(repository.lastTvDetailId) // TV detail should NOT have been called
    }

    @Test
    fun `GetTmdbDetailUseCase - isTv true calls getTvDetail`() = runTest {
        // Arrange
        val expectedDetail = TmdbMovieDetailDto(
            id = 99,
            name = "Breaking Bad",
            overview = "A TV show about chemistry"
        )
        repository.tvDetailResult = NetworkResult.Success(expectedDetail)

        // Act
        val result = getTmdbDetailUseCase(tmdbId = 99, isTv = true)

        // Assert
        assertIs<NetworkResult.Success<TmdbMovieDetailDto>>(result)
        assertEquals(99, result.data.id)
        assertEquals("Breaking Bad", result.data.name)
        assertEquals(99, repository.lastTvDetailId)
        assertNull(repository.lastMovieDetailId) // Movie detail should NOT have been called
    }
}
