package com.example.pocketguard.data.repository

import app.cash.turbine.test
import com.example.pocketguard.domain.model.Transaction
import com.example.pocketguard.domain.model.TransactionCategory
import com.example.pocketguard.domain.model.TransactionType
import com.example.pocketguard.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit Tests untuk TransactionRepository
 */
class TransactionRepositoryTest {

    private lateinit var repository: FakeTransactionRepository

    @BeforeTest
    fun setup() {
        repository = FakeTransactionRepository()
    }

    // ==================== INSERT TESTS ====================

    @Test
    fun `insertTransaction should return new transaction id`() = runTest {
        // Arrange
        val transaction = createTestTransaction(description = "Beli Kopi")

        // Act
        val id = repository.insertTransaction(transaction)

        // Assert
        assertTrue(id > 0)
    }

    @Test
    fun `insertTransaction should add transaction to list`() = runTest {
        // Arrange
        val transaction = createTestTransaction(description = "Gaji")

        // Act
        repository.insertTransaction(transaction)

        // Assert
        repository.getAllTransactions().test {
            val transactions = awaitItem()
            assertEquals(1, transactions.size)
            assertEquals("Gaji", transactions.first().description)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== GET TESTS ====================

    @Test
    fun `getAllTransactions should return all records`() = runTest {
        // Arrange
        repository.insertTransaction(createTestTransaction(description = "Makan Siang"))
        repository.insertTransaction(createTestTransaction(description = "Bensin"))

        // Act & Assert
        repository.getAllTransactions().test {
            val transactions = awaitItem()
            assertEquals(2, transactions.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== DELETE TESTS ====================

    @Test
    fun `deleteTransaction should remove record from list`() = runTest {
        // Arrange
        val id = repository.insertTransaction(createTestTransaction(description = "Hapus Saya"))

        // Act
        repository.deleteTransaction(id)

        // Assert
        repository.getAllTransactions().test {
            val transactions = awaitItem()
            assertTrue(transactions.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== HELPER FUNCTIONS ====================

    private fun createTestTransaction(
        id: Long = 0,
        amount: Double = 50000.0,
        description: String = "Test",
        category: TransactionCategory = TransactionCategory.OTHER,
        type: TransactionType = TransactionType.EXPENSE
    ): Transaction {
        return Transaction(
            id = id,
            amount = amount,
            description = description,
            category = category,
            type = type,
            createdAt = Clock.System.now().toEpochMilliseconds()
        )
    }
}

/**
 * Fake Repository untuk Testing
 * Implementasi in-memory yang tidak bergantung pada database asli.
 */
class FakeTransactionRepository : TransactionRepository {

    private val transactions = MutableStateFlow<List<Transaction>>(emptyList())
    private var nextId = 1L

    override fun getAllTransactions(): Flow<List<Transaction>> = transactions

    override suspend fun insertTransaction(transaction: Transaction): Long {
        val id = nextId++
        val newTransaction = transaction.copy(id = id)
        transactions.update { it + newTransaction }
        return id
    }

    override suspend fun deleteTransaction(id: Long) {
        transactions.update { list -> list.filter { it.id != id } }
    }
}