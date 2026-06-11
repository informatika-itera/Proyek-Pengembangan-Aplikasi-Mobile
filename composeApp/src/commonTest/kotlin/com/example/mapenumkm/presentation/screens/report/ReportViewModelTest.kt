package com.example.mapenumkm.presentation.screens.report

import com.example.mapenumkm.domain.repository.TransactionRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import kotlinx.datetime.LocalDate
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class ReportViewModelTest {

    private val transactionRepository: TransactionRepository = mockk()
    private lateinit var viewModel: ReportViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { transactionRepository.getAllTransactions() } returns flowOf(emptyList())
        viewModel = ReportViewModel(transactionRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have zero totals`() = runTest {
        backgroundScope.launch(testDispatcher) { viewModel.uiState.collect {} }
        
        val state = viewModel.uiState.value
        assertEquals(0.0, state.totalSales)
        assertEquals(0, state.totalTransactions)
    }

    @Test
    fun `onFilterSelected should update filter in state`() = runTest {
        backgroundScope.launch(testDispatcher) { viewModel.uiState.collect {} }
        
        viewModel.onFilterSelected(ReportFilter.MONTHLY)
        assertEquals(ReportFilter.MONTHLY, viewModel.uiState.value.selectedFilter)
    }

    @Test
    fun `onDateSelected should update date in state`() = runTest {
        backgroundScope.launch(testDispatcher) { viewModel.uiState.collect {} }
        
        val date = LocalDate(2023, 1, 1)
        viewModel.onDateSelected(date)
        assertEquals(date, viewModel.uiState.value.selectedDate)
    }

    @Test
    fun `onNextDate should increase date but not beyond today`() = runTest {
        backgroundScope.launch(testDispatcher) { viewModel.uiState.collect {} }
        
        // Use a date in the past
        val pastDate = LocalDate(2023, 1, 1)
        viewModel.onDateSelected(pastDate)
        
        viewModel.onFilterSelected(ReportFilter.DAILY)
        viewModel.onNextDate()
        assertEquals(LocalDate(2023, 1, 2), viewModel.uiState.value.selectedDate)
    }

    @Test
    fun `calculateTopProducts should group items correctly`() = runTest {
        val now = kotlinx.datetime.Clock.System.now()
        val transactions = listOf(
            com.example.mapenumkm.domain.model.Transaction(
                id = 1,
                items = listOf(
                    com.example.mapenumkm.domain.model.TransactionItem(1, "Product A", 50.0, 2)
                ),
                subtotal = 100.0,
                total = 100.0,
                paymentAmount = 100.0,
                changeAmount = 0.0,
                createdAt = now
            ),
            com.example.mapenumkm.domain.model.Transaction(
                id = 2,
                items = listOf(
                    com.example.mapenumkm.domain.model.TransactionItem(1, "Product A", 50.0, 1)
                ),
                subtotal = 50.0,
                total = 50.0,
                paymentAmount = 50.0,
                changeAmount = 0.0,
                createdAt = now
            )
        )
        every { transactionRepository.getAllTransactions() } returns flowOf(transactions)
        
        // Re-init viewModel to pick up new transactions flow
        viewModel = ReportViewModel(transactionRepository)
        backgroundScope.launch(testDispatcher) { viewModel.uiState.collect {} }
        
        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertEquals(1, state.topProducts.size)
        assertEquals("Product A", state.topProducts[0].name)
        assertEquals(3, state.topProducts[0].quantity)
    }

    @Test
    fun `weekly filter should show last 7 days data`() = runTest {
        viewModel.onFilterSelected(ReportFilter.WEEKLY)
        backgroundScope.launch(testDispatcher) { viewModel.uiState.collect {} }
        advanceUntilIdle()
        
        assertEquals(7, viewModel.uiState.value.graphData.size)
    }
}
