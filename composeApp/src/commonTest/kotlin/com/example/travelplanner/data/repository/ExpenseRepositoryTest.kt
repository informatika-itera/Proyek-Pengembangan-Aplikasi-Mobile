package com.example.travelplanner.data.repository

import com.example.travelplanner.domain.model.Expense
import com.example.travelplanner.data.local.TravelPlannerDatabase
import com.example.travelplanner.data.local.TravelDatabaseQueries
import io.mockk.*
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class ExpenseRepositoryTest {
    private val database: TravelPlannerDatabase = mockk(relaxed = true)
    private val queries: TravelDatabaseQueries = mockk(relaxed = true)
    private lateinit var repository: ExpenseRepositoryImpl

    @BeforeTest
    fun setUp() {
        every { database.travelDatabaseQueries } returns queries
        repository = ExpenseRepositoryImpl(database)
    }

    @Test
    fun testSaveExpense() = runTest {
        val expense = Expense(
            id = "exp_1",
            tripId = "trip_123",
            namaItem = "Lunch",
            nominal = 50000.0,
            kategori = "Konsumsi",
            createdAt = 123456789L
        )

        coEvery { queries.insertExpense(any(), any(), any(), any(), any(), any()) } just runs

        repository.saveExpense(expense)

        coVerify {
            queries.insertExpense(
                id = "exp_1",
                trip_id = "trip_123",
                nama_item = "Lunch",
                nominal = 50000.0,
                kategori = "Konsumsi",
                created_at = 123456789L
            )
        }
    }

    @Test
    fun testDeleteExpense() = runTest {
        coEvery { queries.deleteExpense(any()) } just runs
        repository.deleteExpense("exp_1")
        coVerify { queries.deleteExpense("exp_1") }
    }
}
