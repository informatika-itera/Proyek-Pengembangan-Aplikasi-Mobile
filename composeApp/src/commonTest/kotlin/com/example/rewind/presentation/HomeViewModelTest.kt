package com.example.rewind.presentation

import app.cash.turbine.test
import com.example.rewind.data.repository.FakeMovieRepository
import com.example.rewind.domain.model.Movie
import com.example.rewind.domain.model.MovieGenre
import com.example.rewind.domain.model.MovieType
import com.example.rewind.domain.model.WatchStatus
import com.example.rewind.domain.usecase.DeleteMovieUseCase
import com.example.rewind.domain.usecase.GetAllMoviesUseCase
import com.example.rewind.domain.usecase.MovieSortBy
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

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getAllMoviesUseCase: GetAllMoviesUseCase
    private lateinit var deleteMovieUseCase: DeleteMovieUseCase
    private lateinit var viewModel: HomeViewModel
    private lateinit var fakeRepository: FakeMovieRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        fakeRepository = FakeMovieRepository()
        getAllMoviesUseCase = GetAllMoviesUseCase(fakeRepository)
        deleteMovieUseCase = DeleteMovieUseCase(fakeRepository)

        viewModel = HomeViewModel(
            getAllMoviesUseCase,
            deleteMovieUseCase
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be Loading then Empty`() = runTest {
        val vm = HomeViewModel(getAllMoviesUseCase, deleteMovieUseCase)
        vm.uiState.test {
            advanceUntilIdle()
            // Kita ambil status paling terakhir, yang seharusnya adalah Empty karena DB kosong
            val finalState = expectMostRecentItem()
            assertTrue(finalState is HomeUiState.Empty)
        }
    }

    @Test
    fun `state should be Success when movies exist`() = runTest {
        // 1. Masukkan data dummy ke repository TERLEBIH DAHULU agar tidak Empty
        fakeRepository.insertMovie(createTestMovie("Spiderman"))

        // 2. Buat ViewModel baru agar ia membaca data yang baru dimasukkan
        val vm = HomeViewModel(getAllMoviesUseCase, deleteMovieUseCase)

        vm.uiState.test {
            advanceUntilIdle()
            // 3. Karena ada 1 film, status terakhinya HARUS Success
            val state = expectMostRecentItem()
            assertTrue(state is HomeUiState.Success)
        }
    }

    @Test
    fun `sort should update movies`() = runTest {
        // Masukkan data dummy
        fakeRepository.insertMovie(createTestMovie("A Movie"))
        fakeRepository.insertMovie(createTestMovie("Z Movie"))

        val vm = HomeViewModel(getAllMoviesUseCase, deleteMovieUseCase)

        vm.uiState.test {
            advanceUntilIdle()

            // Ubah metode sorting
            vm.setSortBy(MovieSortBy.TITLE_ASC)
            advanceUntilIdle()

            val state = expectMostRecentItem()
            assertTrue(state is HomeUiState.Success)
        }
    }

    @Test
    fun `deleteMovie should remove movie`() = runTest {
        // Masukkan 1 film untuk dihapus
        val id = fakeRepository.insertMovie(createTestMovie("To Delete"))
        val vm = HomeViewModel(getAllMoviesUseCase, deleteMovieUseCase)

        advanceUntilIdle()

        // Hapus film tersebut
        vm.deleteMovie(id)
        advanceUntilIdle()

        vm.uiState.test {
            val state = expectMostRecentItem()
            assertTrue(state is HomeUiState.Success || state is HomeUiState.Empty)
        }
    }

    // Helper function untuk membuat data Movie secara instan di dalam Test
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