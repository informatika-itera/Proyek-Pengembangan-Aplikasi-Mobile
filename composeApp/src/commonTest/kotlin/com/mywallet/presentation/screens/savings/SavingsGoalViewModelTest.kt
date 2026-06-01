package com.mywallet.presentation.screens.savings

import com.mywallet.domain.model.SavingsGoal
import com.mywallet.domain.model.TransactionType
import com.mywallet.fakes.FakeSavingsGoalRepository
import com.mywallet.fakes.FakeTransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class SavingsGoalViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: SavingsGoalViewModel
    private lateinit var goalRepo: FakeSavingsGoalRepository
    private lateinit var transactionRepo: FakeTransactionRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        goalRepo = FakeSavingsGoalRepository()
        transactionRepo = FakeTransactionRepository()
        viewModel = SavingsGoalViewModel(goalRepo, transactionRepo)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addGoal should insert goal correctly`() = runTest {
        viewModel.addGoal("Mobil", 5000.0, "Kendaraan", "#FF0000", null)
        
        // Wait for the goal to be inserted and flow to emit
        var goals = emptyList<SavingsGoal>()
        viewModel.goals.take(2).collect { goals = it }

        assertEquals(1, goals.size)
        assertEquals("Mobil", goals[0].title)
    }

    @Test
    fun `updateCurrentAmount should create an expense transaction when saving more`() = runTest {
        val goal = SavingsGoal(1, "Laptop", 1000.0, 0.0, "Elektronik", "#00FF00", null)
        goalRepo.insertGoal(goal)
        
        viewModel.updateCurrentAmount(1, 200.0)
        
        // Wait for goals to update
        var goals = emptyList<SavingsGoal>()
        viewModel.goals.take(2).collect { goals = it }
        
        assertFalse(goals.isEmpty(), "Goals should not be empty")
        assertEquals(200.0, goals[0].currentAmount)

        var transactionList = emptyList<com.mywallet.domain.model.Transaction>()
        transactionRepo.getAllTransactions().take(1).collect { transactionList = it }
        assertEquals(1, transactionList.size)
        assertEquals("Tabungan: Laptop", transactionList[0].title)
        assertEquals(200.0, transactionList[0].amount)
        assertEquals(TransactionType.EXPENSE, transactionList[0].type)
    }

    @Test
    fun `deleteGoal should remove goal correctly`() = runTest {
        viewModel.addGoal("Test", 100.0, "Other", "#000", null)
        
        var currentGoals = emptyList<SavingsGoal>()
        viewModel.goals.take(2).collect { currentGoals = it }
        
        assertFalse(currentGoals.isEmpty(), "Goals should not be empty before deletion")
        val id = currentGoals[0].id
        viewModel.deleteGoal(id)
        
        var finalGoals = emptyList<SavingsGoal>()
        viewModel.goals.take(2).collect { finalGoals = it }
        
        assertTrue(finalGoals.isEmpty())
    }
}