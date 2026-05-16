package com.example.pocketguard.presentation

import app.cash.turbine.test
import com.example.pocketguard.data.repository.FakeTransactionRepository
import com.example.pocketguard.domain.model.Transaction
import com.example.pocketguard.domain.model.TransactionCategory
import com.example.pocketguard.domain.model.TransactionType
import com.example.pocketguard.domain.usecase.DeleteTransactionUseCase
import com.example.pocketguard.domain.usecase.GetAllTransactionsUseCase
import com.example.pocketguard.domain.usecase.TransactionSortBy
import com.example.pocketguard.presentation.screens.home.HomeUiState
import com.example.pocketguard.presentation.screens.home.HomeViewModel
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

/**
 * Unit Tests untuk HomeViewModel (PocketGuard)
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: FakeTransactionRepository
    private lateinit var getAllTransactionsUseCase: GetAllTransactionsUseCase
    private lateinit var deleteTransactionUseCase: DeleteTransactionUseCase
    private lateinit var viewModel: HomeViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        repository = FakeTransactionRepository()
        getAllTransactionsUseCase = GetAllTransactionsUseCase(repository)
        deleteTransactionUseCase = DeleteTransactionUseCase(repository)

        viewModel = HomeViewModel(
            getAllTransactionsUseCase = getAllTransactionsUseCase,
            deleteTransactionUseCase = deleteTransactionUseCase
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ==================== UI STATE TESTS ====================

    @Test
    fun `initial state should be Loading then Empty`() = runTest {
        viewModel.uiState.test {
            // State awal: Loading
            val loading = awaitItem()
            assertTrue(loading is HomeUiState.Loading)

            // Setelah database kosong dimuat: Empty
            advanceUntilIdle()
            val empty = awaitItem()
            assertTrue(empty is HomeUiState.Empty)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `state should be Success when transactions exist`() = runTest {
        // Arrange
        repository.insertTransaction(createTestTransaction("Makan Siang"))
        repository.insertTransaction(createTestTransaction("Beli Bensin"))

        // Act & Assert
        viewModel.uiState.test {
            skipItems(1) // Lewati Loading
            advanceUntilIdle()

            val state = awaitItem()
            assertTrue(state is HomeUiState.Success)
            assertEquals(2, (state as HomeUiState.Success).transactions.size)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== SEARCH & FILTER TESTS ====================

    @Test
    fun `search should filter transactions by description`() = runTest {
        // Arrange
        repository.insertTransaction(createTestTransaction("Nasi Goreng"))
        repository.insertTransaction(createTestTransaction("Ojek Online"))

        viewModel.uiState.test {
            skipItems(1) // Loading
            advanceUntilIdle()
            skipItems(1) // Success awal (semua item)

            // Act: Cari "Nasi"
            viewModel.onSearchQueryChange("Nasi")
            advanceUntilIdle()

            // Assert
            val state = awaitItem()
            assertTrue(state is HomeUiState.Success)
            assertEquals(1, state.transactions.size)
            assertEquals("Nasi Goreng", state.transactions.first().description)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `category filter should filter transactions`() = runTest {
        // Arrange
        repository.insertTransaction(createTestTransaction("Bakso", category = TransactionCategory.FOOD))
        repository.insertTransaction(createTestTransaction("Tiket Kereta", category = TransactionCategory.TRANSPORT))

        viewModel.uiState.test {
            skipItems(1) // Loading
            advanceUntilIdle()
            skipItems(1) // Success awal

            // Act: Filter kategori "Makanan" (FOOD)
            viewModel.onCategorySelected(TransactionCategory.FOOD)
            advanceUntilIdle()

            // Assert
            val state = awaitItem()
            assertTrue(state is HomeUiState.Success)
            assertEquals(1, state.transactions.size)
            assertEquals(TransactionCategory.FOOD, state.transactions.first().category)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== ACTION TESTS ====================

    @Test
    fun `deleteTransaction should remove record`() = runTest {
        // Arrange
        val id = repository.insertTransaction(createTestTransaction("Hapus Saya"))

        // Act
        viewModel.deleteTransaction(id)
        advanceUntilIdle()

        // Assert
        repository.getAllTransactions().test {
            val transactions = awaitItem()
            assertTrue(transactions.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== HELPER FUNCTIONS ====================

    private fun createTestTransaction(
        description: String,
        category: TransactionCategory = TransactionCategory.OTHER,
        amount: Double = 20000.0
    ): Transaction {
        return Transaction(
            id = 0,
            amount = amount,
            description = description,
            category = category,
            type = TransactionType.EXPENSE,
            createdAt = Clock.System.now().toEpochMilliseconds()
        )
    }
}