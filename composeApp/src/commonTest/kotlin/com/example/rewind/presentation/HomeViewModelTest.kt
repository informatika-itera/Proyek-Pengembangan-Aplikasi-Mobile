package com.example.rewind.presentation

import app.cash.turbine.test
import com.example.rewind.core.network.NetworkResult
import com.example.rewind.data.remote.dto.TmdbMovieDetailDto
import com.example.rewind.data.remote.dto.TmdbMovieDto
import com.example.rewind.data.repository.FakeMovieRepository
import com.example.rewind.domain.model.Movie
import com.example.rewind.domain.model.MovieGenre
import com.example.rewind.domain.model.MovieType
import com.example.rewind.domain.model.WatchStatus
import com.example.rewind.domain.repository.TmdbRepository
import com.example.rewind.domain.usecase.DeleteMovieUseCase
import com.example.rewind.domain.usecase.GetAllMoviesUseCase
import com.example.rewind.domain.usecase.GetTrendingUseCase
import com.example.rewind.domain.usecase.MovieSortBy
import com.example.rewind.domain.usecase.SaveMovieUseCase
import com.example.rewind.domain.usecase.SearchTmdbUseCase
import com.example.rewind.presentation.screens.home.HomeUiState
import com.example.rewind.presentation.screens.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

// Implementasi Fake untuk TmdbRepository khusus untuk testing
class FakeTmdbRepository : TmdbRepository {
    override suspend fun searchMulti(query: String, page: Int): NetworkResult<List<TmdbMovieDto>> = NetworkResult.Success(emptyList())
    override suspend fun getMovieDetail(tmdbId: Int): NetworkResult<TmdbMovieDetailDto> = NetworkResult.Error("Not implemented")
    override suspend fun getTvDetail(tmdbId: Int): NetworkResult<TmdbMovieDetailDto> = NetworkResult.Error("Not implemented")
    override suspend fun getTrending(): NetworkResult<List<TmdbMovieDto>> = NetworkResult.Success(emptyList())
}

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getAllMoviesUseCase: GetAllMoviesUseCase
    private lateinit var deleteMovieUseCase: DeleteMovieUseCase
    private lateinit var searchTmdbUseCase: SearchTmdbUseCase
    private lateinit var saveMovieUseCase: SaveMovieUseCase
    private lateinit var getTrendingUseCase: GetTrendingUseCase

    private lateinit var viewModel: HomeViewModel
    private lateinit var fakeRepository: FakeMovieRepository
    private lateinit var fakeTmdbRepository: FakeTmdbRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        fakeRepository = FakeMovieRepository()
        fakeTmdbRepository = FakeTmdbRepository()
        
        getAllMoviesUseCase = GetAllMoviesUseCase(fakeRepository)
        deleteMovieUseCase = DeleteMovieUseCase(fakeRepository)
        searchTmdbUseCase = SearchTmdbUseCase(fakeTmdbRepository)
        saveMovieUseCase = SaveMovieUseCase(fakeRepository)
        getTrendingUseCase = GetTrendingUseCase(fakeTmdbRepository)

        viewModel = HomeViewModel(
            getAllMoviesUseCase,
            deleteMovieUseCase,
            searchTmdbUseCase,
            saveMovieUseCase,
            getTrendingUseCase
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be Loading then Empty`() = runTest {
        val vm = HomeViewModel(
            getAllMoviesUseCase,
            deleteMovieUseCase,
            searchTmdbUseCase,
            saveMovieUseCase,
            getTrendingUseCase
        )
        vm.uiState.test {
            advanceUntilIdle()
            val finalState = expectMostRecentItem()
            assertTrue(finalState is HomeUiState.Empty)
        }
    }

    @Test
    fun `state should be Success when movies exist`() = runTest {
        fakeRepository.insertMovie(createTestMovie("Spiderman"))

        val vm = HomeViewModel(
            getAllMoviesUseCase,
            deleteMovieUseCase,
            searchTmdbUseCase,
            saveMovieUseCase,
            getTrendingUseCase
        )

        vm.uiState.test {
            advanceUntilIdle()
            val state = expectMostRecentItem()
            assertTrue(state is HomeUiState.Success)
        }
    }

    @Test
    fun `sort should update movies`() = runTest {
        fakeRepository.insertMovie(createTestMovie("A Movie"))
        fakeRepository.insertMovie(createTestMovie("Z Movie"))

        val vm = HomeViewModel(
            getAllMoviesUseCase,
            deleteMovieUseCase,
            searchTmdbUseCase,
            saveMovieUseCase,
            getTrendingUseCase
        )

        vm.uiState.test {
            advanceUntilIdle()

            vm.setSortBy(MovieSortBy.TITLE_ASC)
            advanceUntilIdle()

            val state = expectMostRecentItem()
            assertTrue(state is HomeUiState.Success)
        }
    }

    @Test
    fun `deleteMovie should remove movie`() = runTest {
        val id = fakeRepository.insertMovie(createTestMovie("To Delete"))

        val vm = HomeViewModel(
            getAllMoviesUseCase,
            deleteMovieUseCase,
            searchTmdbUseCase,
            saveMovieUseCase,
            getTrendingUseCase
        )

        vm.uiState.test {
            advanceUntilIdle()
            val initialState = expectMostRecentItem()
            assertTrue(initialState is HomeUiState.Success)

            vm.deleteMovie(id)
            advanceUntilIdle()

            val stateAfterDelete = expectMostRecentItem()
            assertTrue(stateAfterDelete is HomeUiState.Empty)
        }
    }

    private fun createTestMovie(title: String): Movie {
        return Movie(
            id = 0,
            title = title,
            genre = MovieGenre.OTHER,
            type = MovieType.MOVIE,
            status = WatchStatus.PLAN_TO_WATCH,
            rating = null,
            review = "",
            totalEpisodes = null,
            watchedEpisodes = 0,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
    }
}