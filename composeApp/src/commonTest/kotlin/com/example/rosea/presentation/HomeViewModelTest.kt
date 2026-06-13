package com.example.rosea.presentation

import app.cash.turbine.test
import com.example.rosea.domain.model.Product
import com.example.rosea.domain.repository.ProductRepository
import com.example.rosea.presentation.screens.home.HomeUiState
import com.example.rosea.presentation.screens.home.HomeViewModel
import com.example.rosea.presentation.screens.home.SortOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.StandardTestDispatcher
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
        val product = createTestProduct(id = 1, name = "Product A")
        repository.insertProduct(product)
        
        viewModel.uiState.test {
            skipItems(1) // Skip Loading
            
            // Advance time for debounce in HomeViewModel
            testScheduler.advanceTimeBy(301)
            
            val state = awaitItem()
            assertTrue(state is HomeUiState.Success)
            assertEquals(1, (state as HomeUiState.Success).products.size)
            assertEquals("Product A", state.products.first().name)
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

class FakeProductRepository : ProductRepository {
    private val productsFlow = MutableStateFlow<List<Product>>(emptyList())

    override fun getAllProducts(): Flow<List<Product>> = productsFlow

    override suspend fun getProductById(id: Long): Product? {
        return productsFlow.value.find { it.id == id }
    }

    override fun searchProducts(query: String): Flow<List<Product>> {
        return productsFlow.map { list -> 
            list.filter { it.name.contains(query, ignoreCase = true) } 
        }
    }

    override fun getProductsByCategory(category: String): Flow<List<Product>> {
        return productsFlow.map { list -> 
            list.filter { it.category == category } 
        }
    }

    override suspend fun insertProduct(product: Product) {
        productsFlow.update { it + product }
    }

    override suspend fun deleteAllProducts() {
        productsFlow.value = emptyList()
    }
}
