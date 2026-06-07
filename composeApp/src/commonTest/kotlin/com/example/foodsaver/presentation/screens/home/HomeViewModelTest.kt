package com.example.foodsaver.presentation.screens.home

import com.example.foodsaver.domain.model.FoodItem
import com.example.foodsaver.domain.repository.FoodRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import kotlinx.datetime.Clock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: HomeViewModel
    private lateinit var repository: FoodRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        val now = Clock.System.now()
        val items = listOf(
            FoodItem(1, "Apel", 1.0, "pcs", now, now, "Buah"),
            FoodItem(2, "Sawi", 1.0, "pcs", now, now, "Sayur")
        )
        
        repository = object : FoodRepository {
            override fun getAllFoodItems() = flowOf(items)
            override suspend fun getFoodItemById(id: Long) = items.find { it.id == id }
            override suspend fun insertFoodItem(foodItem: FoodItem) {}
            override suspend fun deleteFoodItem(id: Long) {}
            override suspend fun updateFoodItem(foodItem: FoodItem) {}
        }
        
        viewModel = HomeViewModel(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should load items from repository`() = runTest {
        advanceUntilIdle()
        assertEquals(2, viewModel.state.value.items.size)
    }

    @Test
    fun `search query change should filter items`() = runTest {
        advanceUntilIdle()
        viewModel.onSearchQueryChange("Apel")
        assertEquals(1, viewModel.state.value.filteredItems.size)
        assertEquals("Apel", viewModel.state.value.filteredItems[0].name)
    }

    @Test
    fun `category change should filter items`() = runTest {
        advanceUntilIdle()
        viewModel.onCategoryChange("Sayur")
        assertEquals(1, viewModel.state.value.filteredItems.size)
        assertEquals("Sayur", viewModel.state.value.filteredItems[0].category)
    }
}
