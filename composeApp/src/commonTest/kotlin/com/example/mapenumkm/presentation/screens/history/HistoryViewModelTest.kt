package com.example.mapenumkm.presentation.screens.history

import com.example.mapenumkm.domain.repository.TransactionRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {

    private val transactionRepository: TransactionRepository = mockk()
    private lateinit var viewModel: HistoryViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { transactionRepository.getAllTransactions() } returns flowOf(emptyList())
        viewModel = HistoryViewModel(transactionRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should load transactions`() = runTest {
        val job = backgroundScope.launch(testDispatcher) { viewModel.uiState.collect {} }
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is HistoryUiState.Success)
        job.cancel()
    }

    @Test
    fun `onFilterSelected updates state`() = runTest {
        val job = backgroundScope.launch(testDispatcher) { viewModel.uiState.collect {} }
        viewModel.onFilterSelected(HistoryFilter.THIS_WEEK)
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        if (state is HistoryUiState.Success) {
            assertEquals(HistoryFilter.THIS_WEEK, state.selectedFilter)
        }
        job.cancel()
    }
}
