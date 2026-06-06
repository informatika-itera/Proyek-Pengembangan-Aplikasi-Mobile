package com.example.rewind.presentation

import com.example.rewind.data.repository.FakeMovieRepository
import com.example.rewind.domain.model.Movie
import com.example.rewind.domain.model.MovieGenre
import com.example.rewind.domain.model.MovieType
import com.example.rewind.domain.model.WatchStatus
import com.example.rewind.domain.usecase.DeleteMovieUseCase
import com.example.rewind.domain.usecase.GetMovieByIDUseCase
import com.example.rewind.presentation.screens.detail.DetailUiState
import com.example.rewind.presentation.screens.detail.DetailViewModel
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
import kotlin.test.assertEquals
import kotlin.test.assertTrue

//Unit Test
@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeMovieRepository
    private lateinit var viewModel: DetailViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeMovieRepository()
        viewModel = DetailViewModel(
            getMovieByID = GetMovieByIDUseCase(fakeRepository),
            deleteMovie = DeleteMovieUseCase(fakeRepository)
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial uiState is Loading`() = runTest {
        assertEquals(DetailUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `loadMovie emits Success when movie exists`() = runTest {
        val id = fakeRepository.insertMovie(createTestMovie("Parasite"))

        viewModel.loadMovie(id)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is DetailUiState.Success)
        assertEquals("Parasite", (state as DetailUiState.Success).movie.title)
    }

    @Test
    fun `loadMovie emits NotFound when movie does not exist`() = runTest {
        viewModel.loadMovie(999L)
        advanceUntilIdle()

        assertEquals(DetailUiState.NotFound, viewModel.uiState.value)
    }

    private fun createTestMovie(title: String = "Test Movie") = Movie(
        id = 0,
        title = title,
        genre = MovieGenre.DRAMA,
        type = MovieType.MOVIE,
        status = WatchStatus.COMPLETED,
        rating = 4.5f,
        review = "A masterpiece.",
        totalEpisodes = null,
        watchedEpisodes = 0,
        createdAt = Clock.System.now(),
        updatedAt = Clock.System.now()
    )
}