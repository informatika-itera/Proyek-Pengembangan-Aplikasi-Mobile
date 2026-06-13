package com.example.fintrack.presentation

import app.cash.turbine.test
import com.example.fintrack.data.repository.FakeTransactionRepository
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.model.TransactionType
import com.example.fintrack.presentation.screens.home.HomeViewModel
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

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    
    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var repository: FakeTransactionRepository
    private lateinit var viewModel: HomeViewModel
    
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeTransactionRepository()
        val dummyAiRepository = object : com.example.fintrack.domain.repository.AIRepository {
            override suspend fun summarize(text: String): Result<String> = Result.success("")
            override suspend fun generateIdeas(topic: String): Result<List<String>> = Result.success(emptyList())
            override suspend fun improveWriting(text: String, style: com.example.fintrack.domain.repository.WritingStyle): Result<String> = Result.success("")
            override suspend fun translate(text: String, targetLanguage: String): Result<String> = Result.success("")
            override suspend fun chat(message: String): Result<String> = Result.success("")
            override suspend fun suggestTitle(content: String): Result<String> = Result.success("")
            override suspend fun getFinancialInsight(
                totalBalance: Double,
                totalIncome: Double,
                totalExpense: Double,
                budget: Double,
                topCategory: String,
                transactionDetails: String
            ): Result<String> = Result.success("")
        }
        viewModel = HomeViewModel(repository, dummyAiRepository)
    }
    
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `recentTransactions should emit transactions from repository`() = runTest {
        repository.insertTransaction(createTestTransaction("Kopi"))
        
        viewModel.recentTransactions.test {
            advanceUntilIdle()
            val list = expectMostRecentItem()
            assertEquals(1, list.size)
            assertEquals("Kopi", list.first().title)
        }
    }
    
    @Test
    fun `totalBalance should show sum of transactions`() = runTest {
        repository.insertTransaction(createTestTransaction("Gaji", amount = 100.0, type = TransactionType.INCOME))
        repository.insertTransaction(createTestTransaction("Makan", amount = 30.0, type = TransactionType.EXPENSE))
        
        viewModel.totalBalance.test {
            advanceUntilIdle()
            val balance = expectMostRecentItem()
            assertEquals(70.0, balance)
        }
    }
    
    private fun createTestTransaction(
        title: String,
        amount: Double = 10.0,
        type: TransactionType = TransactionType.EXPENSE
    ): Transaction {
        return Transaction(
            id = 0,
            title = title,
            amount = amount,
            type = type,
            category = "Food",
            date = Clock.System.now(),
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
    }
}
