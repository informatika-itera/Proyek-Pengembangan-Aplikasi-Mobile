package com.example.pocketguard.domain.usecase

import app.cash.turbine.test
import com.example.pocketguard.data.repository.FakeTransactionRepository
import com.example.pocketguard.domain.model.Transaction
import com.example.pocketguard.domain.model.TransactionCategory
import com.example.pocketguard.domain.model.TransactionType
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TransactionUseCaseTest {

    private lateinit var repository: FakeTransactionRepository
    private lateinit var saveUseCase: SaveTransactionUseCase
    private lateinit var getAllUseCase: GetAllTransactionsUseCase

    @BeforeTest
    fun setup() {
        repository = FakeTransactionRepository()
        saveUseCase = SaveTransactionUseCase(repository)
        getAllUseCase = GetAllTransactionsUseCase(repository)
    }

    // --- TEST 1: Validasi Simpan Berhasil ---
    @Test
    fun `SaveTransactionUseCase should return success for valid data`() = runTest {
        val transaction = createTestTransaction(amount = 1000.0, description = "Valid")
        val result = saveUseCase(transaction)
        assertTrue(result.isSuccess)
    }

    // --- TEST 2: Validasi Nominal Harus > 0 ---
    @Test
    fun `SaveTransactionUseCase should return failure for zero or negative amount`() = runTest {
        val transaction = createTestTransaction(amount = 0.0, description = "Invalid")
        val result = saveUseCase(transaction)
        assertTrue(result.isFailure)
        assertEquals("Nominal harus lebih dari 0", result.exceptionOrNull()?.message)
    }

    // --- TEST 3: Validasi Deskripsi Kosong ---
    @Test
    fun `SaveTransactionUseCase should return failure for empty description`() = runTest {
        val transaction = createTestTransaction(amount = 5000.0, description = "")
        val result = saveUseCase(transaction)
        assertTrue(result.isFailure)
        assertEquals("Deskripsi tidak boleh kosong", result.exceptionOrNull()?.message)
    }

    // --- TEST 4: Pengurutan Nominal Terbesar ---
    @Test
    fun `GetAllTransactionsUseCase should sort by amount descending correctly`() = runTest {
        // Simpan dua data dengan nominal berbeda
        repository.insertTransaction(createTestTransaction(amount = 100.0, description = "Kecil"))
        repository.insertTransaction(createTestTransaction(amount = 500.0, description = "Besar"))

        getAllUseCase(TransactionSortBy.AMOUNT_DESC).test {
            val list = awaitItem()
            assertEquals(500.0, list.first().amount) // Data pertama harus yang nominalnya 500
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createTestTransaction(amount: Double, description: String): Transaction {
        return Transaction(
            amount = amount,
            description = description,
            category = TransactionCategory.OTHER,
            type = TransactionType.EXPENSE,
            createdAt = Clock.System.now().toEpochMilliseconds()
        )
    }
}