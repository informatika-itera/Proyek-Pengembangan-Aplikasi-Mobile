package com.example.inventra.presentation

import app.cash.turbine.test
import com.example.inventra.data.repository.FakeItemRepository
import com.example.inventra.domain.model.BorrowRecord
import com.example.inventra.domain.model.BorrowStatus
import com.example.inventra.domain.model.Item
import com.example.inventra.domain.model.ItemCategory
import com.example.inventra.domain.model.ItemCondition
import com.example.inventra.domain.repository.BorrowRepository
import com.example.inventra.presentation.screens.dashboard.DashboardUiState
import com.example.inventra.presentation.screens.dashboard.DashboardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
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

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var itemRepository: FakeItemRepository
    private lateinit var borrowRepository: FakeBorrowRepository
    private lateinit var viewModel: DashboardViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        itemRepository = FakeItemRepository()
        borrowRepository = FakeBorrowRepository()
        viewModel = DashboardViewModel(itemRepository, borrowRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be Loading`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is DashboardUiState.Loading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `state should show correct totalItems count`() = runTest {
        itemRepository.insertItem(createTestItem("Item 1"))
        itemRepository.insertItem(createTestItem("Item 2"))

        viewModel.uiState.test {
            skipItems(1)
            advanceUntilIdle()

            val state = awaitItem()
            assertTrue(state is DashboardUiState.Success)
            assertEquals(2, (state as DashboardUiState.Success).totalItems)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `state should show overdueItems count correctly`() = runTest {
        borrowRepository.addRecord(createOverdueRecord())

        viewModel.uiState.test {
            skipItems(1)
            advanceUntilIdle()

            val state = awaitItem()
            assertTrue(state is DashboardUiState.Success)
            assertEquals(1, (state as DashboardUiState.Success).overdueItems)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `state should show empty activeBorrowings when no records`() = runTest {
        viewModel.uiState.test {
            skipItems(1)
            advanceUntilIdle()

            val state = awaitItem()
            assertTrue(state is DashboardUiState.Success)
            assertTrue((state as DashboardUiState.Success).activeBorrowings.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createTestItem(name: String): Item {
        return Item(
            id = 0,
            name = name,
            category = ItemCategory.OTHER,
            location = "Office",
            totalStock = 5,
            availableStock = 5,
            condition = ItemCondition.GOOD,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
    }

    private fun createOverdueRecord(): BorrowRecord {
        val now = Clock.System.now()
        return BorrowRecord(
            id = 1,
            itemId = 1,
            itemName = "Test Item",
            borrowerName = "Test User",
            borrowDate = now,
            dueDate = now,
            status = BorrowStatus.OVERDUE
        )
    }
}

class FakeBorrowRepository : BorrowRepository {
    private val records = MutableStateFlow<List<BorrowRecord>>(emptyList())

    fun addRecord(record: BorrowRecord) {
        records.update { it + record }
    }

    override fun getAllRecords(): Flow<List<BorrowRecord>> = records

    override fun getActiveRecords(): Flow<List<BorrowRecord>> = records.map { list ->
        list.filter { it.status == BorrowStatus.ACTIVE || it.status == BorrowStatus.OVERDUE }
    }

    override suspend fun borrowItem(record: BorrowRecord): Long {
        records.update { it + record }
        return record.id
    }

    override suspend fun returnItem(recordId: Long) {
        records.update { list ->
            list.map { if (it.id == recordId) it.copy(status = BorrowStatus.RETURNED) else it }
        }
    }
}