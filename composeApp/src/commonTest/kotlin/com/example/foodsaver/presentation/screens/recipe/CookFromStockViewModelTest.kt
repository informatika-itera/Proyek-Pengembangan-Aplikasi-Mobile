package com.example.foodsaver.presentation.screens.recipe

import com.example.foodsaver.domain.model.*
import com.example.foodsaver.domain.repository.FoodRepository
import com.example.foodsaver.domain.repository.RecipeRepository
import com.example.foodsaver.domain.engine.RuleBasedRecipeEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
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
    private lateinit var foodRepository: FoodRepository
    private lateinit var recipeRepository: RecipeRepository
    private val ruleBasedEngine = RuleBasedRecipeEngine()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        val items = listOf(
            FoodItem(1, "Nasi", 1.0, "pcs", Clock.System.now(), Clock.System.now(), "Lainnya"),
            FoodItem(2, "Telur", 2.0, "pcs", Clock.System.now(), Clock.System.now(), "Susu & Telur")
        )
        
        foodRepository = object : FoodRepository {
            override fun getAllFoodItems() = flowOf(items)
            override suspend fun getFoodItemById(id: Long) = items.find { it.id == id }
            override suspend fun insertFoodItem(foodItem: FoodItem) {}
            override suspend fun deleteFoodItem(id: Long) {}
            override suspend fun updateFoodItem(foodItem: FoodItem) {}
        }

        recipeRepository = object : RecipeRepository {
            override suspend fun searchRecipesByIngredients(ingredients: List<String>): List<Recipe> = emptyList()
            override suspend fun getRecipeDetails(id: String): Recipe? = null
            override suspend fun searchRecipesByName(query: String): List<Recipe> = emptyList()
            
            override suspend fun getRecommendations(
                ingredients: List<RecipeIngredient>,
                preference: String,
                prioritizeExpiring: Boolean
            ): Result<RecipeRecommendation> {
                // Return fallback for testing
                return Result.success(ruleBasedEngine.generateRecommendations(ingredients, preference, prioritizeExpiring))
            }

            override suspend fun generateAiRecipe(
                ingredients: List<RecipeIngredient>,
                preference: String,
                prioritizeExpiring: Boolean
            ): Result<RecipeRecommendation> {
                return Result.failure(Exception("Gemini not used in CookFromStock anymore"))
            }
            
            override fun getFavoriteRecipes(): Flow<List<Recipe>> = flowOf(emptyList())
            override suspend fun toggleFavorite(recipe: Recipe) {}
            override suspend fun isFavorite(id: String): Boolean = false
        }
        
        viewModel = CookFromStockViewModel(foodRepository, recipeRepository)
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
    fun `generateRecommendation should set Success or Fallback state`() = runTest {
        // Prepare selection
        viewModel.toggleIngredientSelection(1L) // Nasi
        viewModel.toggleIngredientSelection(2L) // Telur
        
        viewModel.generateRecommendation(listOf(1L, 2L), emptyList(), true, "Praktis")
        advanceUntilIdle()
        
        val state = viewModel.state.value.recommendationState
        assertTrue(state is RecipeUiState.Fallback || state is RecipeUiState.Success)
        
        if (state is RecipeUiState.Fallback) {
            assertEquals("Nasi Goreng Telur", state.recommendation.title)
        } else if (state is RecipeUiState.Success) {
            assertEquals("Nasi Goreng Telur", state.recommendation.title)
        }
    }

    @Test
    fun `generateRecommendation should show validation error if no ingredients selected`() = runTest {
        viewModel.generateRecommendation(emptyList(), emptyList(), true, "Praktis")
        advanceUntilIdle()
        assertEquals("Pilih atau masukkan minimal satu bahan terlebih dahulu.", viewModel.state.value.validationError)
        assertEquals(RecipeUiState.Idle, viewModel.state.value.recommendationState)
    }
}
