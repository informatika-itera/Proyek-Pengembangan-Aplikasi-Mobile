package com.example.tabungin.presentation

import app.cash.turbine.test
import com.example.tabungin.data.repository.FakeTargetRepository
import com.example.tabungin.domain.model.Target
import com.example.tabungin.domain.usecase.DeleteTargetUseCase
import com.example.tabungin.domain.usecase.GetAllTargetsUseCase
import com.example.tabungin.presentation.screens.home.HomeUiState
import com.example.tabungin.presentation.screens.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue


@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repo: FakeTargetRepository
    private lateinit var viewModel: HomeViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repo = FakeTargetRepository()
        viewModel = HomeViewModel(
            getAllTargetsUseCase = GetAllTargetsUseCase(repo),
            deleteTargetUseCase  = DeleteTargetUseCase(repo)
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state isLoading false dan targets kosong`() = runTest {
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.targets.isEmpty())
    }

    @Test
    fun `deleteTarget menghapus target dari uiState`() = runTest {

        val id = repo.insertTarget(
            Target(nama = "Test", targetAmount = 1_000_000.0, deadline = "2025-12-31")
        )

        val vm = HomeViewModel(
            GetAllTargetsUseCase(repo),
            DeleteTargetUseCase(repo)
        )
        vm.uiState.test {

            val initialState = awaitItem()
            assertTrue(initialState.targets.any { it.id == id }, "Data harusnya ada di awal")

            vm.deleteTarget(id)

            val updatedState = awaitItem()
            assertTrue(updatedState.targets.none { it.id == id }, "Data harusnya sudah terhapus dari uiState")

            cancelAndIgnoreRemainingEvents()}
    }

    @Test
    fun `totalTerkumpul dihitung dengan benar`() = runTest {
        repo.insertTarget(Target(nama = "A", targetAmount = 10_000.0, deadline = "2025-12-31", terkumpul = 3_000.0))
        repo.insertTarget(Target(nama = "B", targetAmount = 20_000.0, deadline = "2025-12-31", terkumpul = 7_000.0))

        val vm = HomeViewModel(GetAllTargetsUseCase(repo), DeleteTargetUseCase(repo))
        advanceUntilIdle()

        assertEquals(10_000.0, vm.uiState.value.totalTerkumpul)
    }

    @Test
    fun `clearError mengosongkan error di uiState`() = runTest {
        assertFails { throw IllegalStateException("dummy") }
        viewModel.clearError()
        assertNull(viewModel.uiState.value.error)
    }
}
