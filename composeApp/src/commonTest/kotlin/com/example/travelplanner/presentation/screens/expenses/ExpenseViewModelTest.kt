package com.example.travelplanner.presentation.screens.expenses

import com.example.travelplanner.domain.model.Expense
import com.example.travelplanner.domain.model.Trip
import com.example.travelplanner.domain.repository.TripRepository
import com.example.travelplanner.domain.repository.ExpenseRepository
import com.example.travelplanner.domain.usecase.ExtractExpenseUseCase
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class ExpenseViewModelTest {
    private val tripRepository: TripRepository = mockk(relaxed = true)
    private val expenseRepository: ExpenseRepository = mockk(relaxed = true)
    private val extractExpenseUseCase: ExtractExpenseUseCase = mockk(relaxed = true)
    private lateinit var viewModel: ExpenseViewModel

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ExpenseViewModel(tripRepository, expenseRepository, extractExpenseUseCase)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitializeTripSuccess() = runTest(testDispatcher) {
        val trip = Trip(
            id = "trip_123",
            destination = "Bali",
            startDate = "1 Jun",
            endDate = "5 Jun",
            duration = "Jakarta|1 Jun – 5 Jun",
            vibe = "Beach",
            itineraryItems = emptyList()
        )
        val expenses = listOf(
            Expense(
                id = "exp_1",
                tripId = "trip_123",
                namaItem = "Kopi",
                nominal = 25000.0,
                kategori = "Konsumsi",
                createdAt = 123456789L
            )
        )

        every { tripRepository.getTripById("trip_123") } returns flowOf(trip)
        every { expenseRepository.getExpensesForTrip("trip_123") } returns flowOf(expenses)
        every { expenseRepository.getTotalExpensesForTrip("trip_123") } returns flowOf(25000.0)

        viewModel.initializeTrip("trip_123")

        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(trip, viewModel.uiState.value.trip)
        assertEquals(expenses, viewModel.uiState.value.expenses)
        assertEquals(25000.0, viewModel.uiState.value.totalExpenses)
    }

    @Test
    fun testAddManualExpense() = runTest(testDispatcher) {
        val trip = Trip(
            id = "trip_123",
            destination = "Bali",
            startDate = "1 Jun",
            endDate = "5 Jun",
            duration = "Jakarta|1 Jun – 5 Jun",
            vibe = "Beach",
            itineraryItems = emptyList()
        )
        every { tripRepository.getTripById("trip_123") } returns flowOf(trip)
        every { expenseRepository.getExpensesForTrip("trip_123") } returns flowOf(emptyList())
        every { expenseRepository.getTotalExpensesForTrip("trip_123") } returns flowOf(0.0)

        viewModel.initializeTrip("trip_123")
        advanceUntilIdle()

        coEvery { expenseRepository.saveExpense(any()) } just runs

        viewModel.addManualExpense("Lunch", 45000.0, "Konsumsi")
        advanceUntilIdle()

        coVerify {
            expenseRepository.saveExpense(match {
                it.namaItem == "Lunch" && it.nominal == 45000.0 && it.kategori == "Konsumsi" && it.tripId == "trip_123"
            })
        }
    }

    @Test
    fun testDeleteExpense() = runTest(testDispatcher) {
        coEvery { expenseRepository.deleteExpense(any()) } just runs

        viewModel.deleteExpense("exp_1")
        advanceUntilIdle()

        coVerify { expenseRepository.deleteExpense("exp_1") }
    }

    @Test
    fun testCategoryFilterWithEmptyStringReturnsAll() = runTest(testDispatcher) {
        val expenses = listOf(
            Expense("1", "trip_123", "Food", 100.0, "Konsumsi", 123L),
            Expense("2", "trip_123", "Taxi", 50.0, "Transportasi", 124L)
        )
        
        val trip = Trip("trip_123", "Bali", "1 Jun", "5 Jun", "Jakarta|1 Jun – 5 Jun", "Beach", emptyList())
        every { tripRepository.getTripById("trip_123") } returns flowOf(trip)
        every { expenseRepository.getExpensesForTrip("trip_123") } returns flowOf(expenses)
        every { expenseRepository.getTotalExpensesForTrip("trip_123") } returns flowOf(150.0)

        viewModel.initializeTrip("trip_123")
        advanceUntilIdle()

        // Set category filter to empty string ("All")
        viewModel.setCategoryFilter("")
        assertEquals(2, viewModel.uiState.value.filteredExpenses.size)

        // Set category filter to specific category
        viewModel.setCategoryFilter("Konsumsi")
        assertEquals(1, viewModel.uiState.value.filteredExpenses.size)
        assertEquals("Food", viewModel.uiState.value.filteredExpenses.first().namaItem)
    }
}
