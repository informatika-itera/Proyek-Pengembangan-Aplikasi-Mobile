package com.mywallet.presentation.screens.stats

import app.cash.turbine.test
import com.mywallet.domain.model.Transaction
import app.cash.turbine.test
import com.mywallet.domain.model.TransactionType
import com.mywallet.fakes.FakeTransactionRepository
import com.mywallet.fakes.FakeUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class StatisticsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: StatisticsViewModel
    private lateinit var transactionRepo: FakeTransactionRepository
    private lateinit var userRepo: FakeUserRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        transactionRepo = FakeTransactionRepository()
        userRepo = FakeUserRepository()
        viewModel = StatisticsViewModel(transactionRepo, userRepo)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be Loading`() = runTest {
        val newRepo = FakeTransactionRepository()
        val vm = StatisticsViewModel(newRepo, userRepo)
        assertTrue(vm.uiState.value is StatisticsUiState.Loading)
    }

    @Test
    fun `Success state should have correct income and expense totals`() = runTest {
        val testData = listOf(
            Transaction(1, "Gaji", 5000.0, TransactionType.INCOME, "Gaji", "20 May 2024", "08:00"),
            Transaction(2, "Makan", 100.0, TransactionType.EXPENSE, "Makanan", "21 May 2024", "12:00")
        )
        transactionRepo.emit(testData)
        
        // Use turbine or collect to wait for the Success state
        viewModel.uiState.test {
            // Skip initial Loading state if it's there
            var state = awaitItem()
            if (state is StatisticsUiState.Loading) {
                state = awaitItem()
            }
            
            assertTrue(state is StatisticsUiState.Success)
            assertEquals(5000.0, (state as StatisticsUiState.Success).totalIncome)
            assertEquals(100.0, state.totalExpense)
            assertEquals(98.0, state.savingsRate)
        }
    }

    @Test
    fun `Category breakdown should be calculated correctly`() = runTest {
        val testData = listOf(
            Transaction(1, "A", 100.0, TransactionType.EXPENSE, "Makanan", "D", "T"),
            Transaction(2, "B", 200.0, TransactionType.EXPENSE, "Makanan", "D", "T"),
            Transaction(3, "C", 50.0, TransactionType.EXPENSE, "Lainnya", "D", "T")
        )
        transactionRepo.emit(testData)
        
        viewModel.uiState.test {
            var state = awaitItem()
            if (state is StatisticsUiState.Loading) {
                state = awaitItem()
            }
            
            assertTrue(state is StatisticsUiState.Success)
            val makananStat = (state as StatisticsUiState.Success).categoryBreakdown.find { it.category == "Makanan" }
            assertEquals(300.0, makananStat?.amount)
            assertEquals(2, state.categoryBreakdown.size)
        }
    }
}