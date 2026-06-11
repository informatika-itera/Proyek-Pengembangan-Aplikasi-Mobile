package com.example.inventra.presentation

import app.cash.turbine.test
import com.example.inventra.FakeAuthRepository
import com.example.inventra.FakeBorrowRepository
import com.example.inventra.FakeItemRepository
import com.example.inventra.domain.model.Item
import com.example.inventra.domain.model.ItemCategory
import com.example.inventra.domain.model.ItemCondition
import com.example.inventra.presentation.screens.detail.ItemDetailUiState
import com.example.inventra.presentation.screens.detail.ItemDetailViewModel
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

@OptIn(ExperimentalCoroutinesApi::class)
class ItemDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var itemRepository: FakeItemRepository
    private lateinit var borrowRepository: FakeBorrowRepository
    private lateinit var authRepository: FakeAuthRepository
    private lateinit var viewModel: ItemDetailViewModel

    private val testItemId = 1L

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        itemRepository = FakeItemRepository()
        borrowRepository = FakeBorrowRepository()
        authRepository = FakeAuthRepository()
        
        viewModel = ItemDetailViewModel(
            itemId = testItemId,
            itemRepository = itemRepository,
            borrowRepository = borrowRepository,
            authRepository = authRepository
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be Loading`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is ItemDetailUiState.Loading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `state should be NotFound when item does not exist`() = runTest {
        viewModel.uiState.test {
            skipItems(1) // Loading
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is ItemDetailUiState.NotFound)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `state should be Success when item exists`() = runTest {
        itemRepository.insertItem(createTestItem(testItemId, "Target Item"))
        
        viewModel.uiState.test {
            skipItems(1) // Loading
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is ItemDetailUiState.Success)
            assertEquals("Target Item", state.item.name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `requestBorrow should add record to repository`() = runTest {
        itemRepository.insertItem(createTestItem(testItemId, "Borrow Me", stock = 5))
        
        viewModel.uiState.test {
            skipItems(1) // Loading
            advanceUntilIdle()
            assertTrue(awaitItem() is ItemDetailUiState.Success)
            
            var successCalled = false
            viewModel.requestBorrow(
                borrowerName = "Tester",
                borrowerDivision = "KONTEN",
                onSuccess = { successCalled = true }
            )
            
            advanceUntilIdle()
            assertTrue(successCalled)
            
            borrowRepository.getAllRecords().test {
                val records = awaitItem()
                assertEquals(1, records.size)
                assertEquals("Tester", records.first().borrowerName)
                assertEquals("Borrow Me", records.first().itemName)
                cancelAndIgnoreRemainingEvents()
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `requestBorrow should fail when stock is 0`() = runTest {
        itemRepository.insertItem(createTestItem(testItemId, "Out of Stock", stock = 0))
        
        viewModel.uiState.test {
            skipItems(1) // Loading
            advanceUntilIdle()
            assertTrue(awaitItem() is ItemDetailUiState.Success)

            var errorMsg = ""
            viewModel.requestBorrow(
                borrowerName = "Tester",
                borrowerDivision = "KONTEN",
                onSuccess = {},
                onError = { errorMsg = it }
            )
            
            advanceUntilIdle()
            assertEquals("Stok barang habis", errorMsg)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createTestItem(id: Long, name: String, stock: Int = 5): Item {
        return Item(
            id = id,
            name = name,
            category = ItemCategory.OTHER,
            location = "Office",
            totalStock = stock,
            availableStock = stock,
            condition = ItemCondition.GOOD,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
    }
}
