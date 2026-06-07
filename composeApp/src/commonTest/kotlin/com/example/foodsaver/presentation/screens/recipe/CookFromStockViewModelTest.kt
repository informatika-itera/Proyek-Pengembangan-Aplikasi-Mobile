package com.example.foodsaver.presentation.screens.recipe

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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CookFromStockViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: CookFromStockViewModel
    private lateinit var repository: FoodRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        val items = listOf(
            FoodItem(1, "Nasi", 1.0, "pcs", Clock.System.now(), Clock.System.now(), "Lainnya"),
            FoodItem(2, "Telur", 2.0, "pcs", Clock.System.now(), Clock.System.now(), "Susu & Telur")
        )
        
        repository = object : FoodRepository {
            override fun getAllFoodItems() = flowOf(items)
            override suspend fun getFoodItemById(id: Long) = items.find { it.id == id }
            override suspend fun insertFoodItem(foodItem: FoodItem) {}
            override suspend fun deleteFoodItem(id: Long) {}
            override suspend fun updateFoodItem(foodItem: FoodItem) {}
        }
        
        viewModel = CookFromStockViewModel(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `toggleIngredientSelection should add or remove id from state`() = runTest {
        viewModel.toggleIngredientSelection(1L)
        assertTrue(viewModel.state.value.selectedIngredientIds.contains(1L))
        
        viewModel.toggleIngredientSelection(1L)
        assertTrue(!viewModel.state.value.selectedIngredientIds.contains(1L))
    }

    @Test
    fun `addManualIngredient should prevent duplicates`() = runTest {
        viewModel.addManualIngredient("Garam")
        viewModel.addManualIngredient("garam") // case insensitive check
        
        assertEquals(1, viewModel.state.value.manualIngredients.size)
        assertEquals("garam", viewModel.state.value.manualIngredients[0])
    }

    @Test
    fun `addManualIngredient with comma should add multiple items`() = runTest {
        viewModel.addManualIngredient("Lada, Kunyit, Jahe")
        assertEquals(3, viewModel.state.value.manualIngredients.size)
    }

    @Test
    fun `generateRecommendation should show error if no ingredients selected`() = runTest {
        viewModel.generateRecommendation(emptyList(), emptyList(), true, "Praktis")
        advanceUntilIdle()
        assertEquals("Pilih atau masukkan minimal satu bahan terlebih dahulu.", viewModel.state.value.error)
    }
}
