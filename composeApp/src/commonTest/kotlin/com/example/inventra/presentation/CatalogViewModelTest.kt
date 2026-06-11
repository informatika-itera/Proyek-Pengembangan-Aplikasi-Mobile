package com.example.inventra.presentation

import androidx.compose.ui.text.input.TextFieldValue
import app.cash.turbine.test
import com.example.inventra.FakeAuthRepository
import com.example.inventra.FakeItemRepository
import com.example.inventra.domain.model.Item
import com.example.inventra.domain.model.ItemCategory
import com.example.inventra.domain.model.ItemCondition
import com.example.inventra.presentation.screens.catalog.CatalogUiState
import com.example.inventra.presentation.screens.catalog.CatalogViewModel
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
class CatalogViewModelTest {
    
    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var repository: FakeItemRepository
    private lateinit var authRepository: FakeAuthRepository
    private lateinit var viewModel: CatalogViewModel
    
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        repository = FakeItemRepository()
        authRepository = FakeAuthRepository()
        
        viewModel = CatalogViewModel(
            itemRepository = repository,
            authRepository = authRepository
        )
    }
    
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `initial state should be Loading then Empty`() = runTest {
        viewModel.uiState.test {
            val loading = awaitItem()
            assertTrue(loading is CatalogUiState.Loading)
            
            advanceUntilIdle()
            val empty = awaitItem()
            assertTrue(empty is CatalogUiState.Empty)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `state should be Success when items exist`() = runTest {
        repository.insertItem(createTestItem("Item 1"))
        
        viewModel.uiState.test {
            skipItems(1) // Loading
            advanceUntilIdle()
            
            val state = awaitItem()
            assertTrue(state is CatalogUiState.Success)
            assertEquals(1, (state as CatalogUiState.Success).items.size)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `search should filter items`() = runTest {
        repository.insertItem(createTestItem("Projector"))
        repository.insertItem(createTestItem("Marker"))
        
        viewModel.uiState.test {
            skipItems(1) // Loading
            advanceUntilIdle()
            skipItems(1) // Initial success (all items)
            
            viewModel.onSearchQueryChange(TextFieldValue("Project"))
            // Debounce is 300ms in CatalogViewModel
            testDispatcher.scheduler.advanceTimeBy(400)
            advanceUntilIdle()
            
            val state = awaitItem()
            assertTrue(state is CatalogUiState.Success)
            assertEquals(1, (state as CatalogUiState.Success).items.size)
            assertEquals("Projector", state.items.first().name)
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `category filter should filter items`() = runTest {
        repository.insertItem(createTestItem("Stethoscope", category = ItemCategory.MEDICAL))
        repository.insertItem(createTestItem("Bread", category = ItemCategory.FOOD))
        
        viewModel.uiState.test {
            skipItems(1) // Loading
            advanceUntilIdle()
            skipItems(1) // Initial success
            
            viewModel.onCategorySelected(ItemCategory.MEDICAL)
            advanceUntilIdle()
            
            val state = awaitItem()
            assertTrue(state is CatalogUiState.Success)
            assertEquals(1, (state as CatalogUiState.Success).items.size)
            assertEquals(ItemCategory.MEDICAL, state.items.first().category)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    private fun createTestItem(
        name: String,
        category: ItemCategory = ItemCategory.OTHER
    ): Item {
        return Item(
            id = 0,
            name = name,
            category = category,
            location = "Office",
            totalStock = 5,
            availableStock = 5,
            condition = ItemCondition.GOOD,
            picName = "Admin",
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
    }
}
