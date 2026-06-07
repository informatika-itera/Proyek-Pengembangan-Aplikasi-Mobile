package com.example.rosea.presentation

import app.cash.turbine.test
import com.example.rosea.data.repository.FakeProductRepository
import com.example.rosea.domain.model.Product
import com.example.rosea.presentation.screens.home.HomeUiState
import com.example.rosea.presentation.screens.home.HomeViewModel
import com.example.rosea.presentation.screens.home.SortOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeProductRepository
    private lateinit var viewModel: HomeViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeProductRepository()
        viewModel = HomeViewModel(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be Loading`() = runTest {
        viewModel.uiState.test {
            assertTrue(awaitItem() is HomeUiState.Loading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should show products when repository has data`() = runTest {
        repository.insertProduct(createTestProduct(id = 1, name = "Product A"))
        
        viewModel.uiState.test {
            skipItems(1) // Skip Loading
            
            // Advance time for initial debounce in combine
            testScheduler.advanceTimeBy(301)
            
            val state = awaitItem()
            assertTrue(state is HomeUiState.Success)
            assertEquals(1, (state as HomeUiState.Success).products.size)
            assertEquals("Product A", state.products.first().name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `search query change should filter products`() = runTest {
        repository.insertProduct(createTestProduct(id = 1, name = "Apple"))
        repository.insertProduct(createTestProduct(id = 2, name = "Banana"))

        viewModel.uiState.test {
            skipItems(1) // Loading
            
            viewModel.onSearchQueryChange("Apple")
            
            // Advance time for debounce (300ms)
            testScheduler.advanceTimeBy(301)
            
            val state = expectMostRecentItem()
            assertTrue(state is HomeUiState.Success)
            assertEquals(1, state.products.size)
            assertEquals("Apple", state.products.first().name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `category selection should filter products`() = runTest {
        repository.insertProduct(createTestProduct(id = 1, category = "Electronics"))
        repository.insertProduct(createTestProduct(id = 2, category = "Food"))

        viewModel.uiState.test {
            skipItems(1) // Loading
            
            // Tunggu debounce awal selesai
            testScheduler.advanceTimeBy(301)
            
            viewModel.onCategorySelect("Electronics")
            
            // Tunggu emisi baru
            testScheduler.advanceTimeBy(1)
            
            val state = expectMostRecentItem()
            assertTrue(state is HomeUiState.Success)
            assertEquals(1, state.products.size)
            assertEquals("Electronics", state.products.first().category)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `sort order change should sort products by price low to high`() = runTest {
        repository.insertProduct(createTestProduct(id = 1, price = 100.0))
        repository.insertProduct(createTestProduct(id = 2, price = 50.0))

        viewModel.uiState.test {
            skipItems(1) // Loading
            
            // Tunggu debounce awal selesai
            testScheduler.advanceTimeBy(301)
            
            viewModel.onSortOrderChange(SortOrder.PRICE_LOW_TO_HIGH)
            
            // Tunggu emisi baru
            testScheduler.advanceTimeBy(1)
            
            val state = expectMostRecentItem()
            assertTrue(state is HomeUiState.Success)
            assertEquals(50.0, state.products[0].price)
            assertEquals(100.0, state.products[1].price)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createTestProduct(
        id: Long = 0,
        name: String = "Test",
        category: String = "Test Category",
        price: Double = 100.0
    ) = Product(
        id = id,
        name = name,
        brand = "Brand",
        description = "Desc",
        price = price,
        category = category,
        imageUrl = "",
        createdAt = 0L,
        updatedAt = 0L
    )
}
