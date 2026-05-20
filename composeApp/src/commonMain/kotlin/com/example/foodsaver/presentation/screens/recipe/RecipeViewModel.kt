package com.example.foodsaver.presentation.screens.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodsaver.domain.model.Recipe
import com.example.foodsaver.domain.usecase.GetRecipesByIngredientsUseCase
import com.example.foodsaver.domain.usecase.SearchRecipesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecipeUiState(
    val isLoading: Boolean = false,
    val recipes: List<Recipe> = emptyList(),
    val filteredRecipes: List<Recipe> = emptyList(),
    val error: String? = null,
    val searchQuery: String = "",
    val selectedIngredients: List<String> = emptyList(),
    val activeFilter: String? = null
)

class RecipeViewModel(
    private val getRecipesByIngredientsUseCase: GetRecipesByIngredientsUseCase,
    private val searchRecipesUseCase: SearchRecipesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RecipeUiState())
    val state: StateFlow<RecipeUiState> = _state.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        if (query.length >= 3) {
            searchRecipes(query)
        }
    }

    private fun searchRecipes(query: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val results = searchRecipesUseCase(query)
                _state.update { 
                    it.copy(
                        isLoading = false, 
                        recipes = results, 
                        filteredRecipes = applyLocalFilter(results, it.activeFilter),
                        error = null 
                    ) 
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun findRecipesByIngredients(ingredients: List<String>) {
        _state.update { it.copy(selectedIngredients = ingredients) }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val results = getRecipesByIngredientsUseCase(ingredients)
                _state.update { 
                    it.copy(
                        isLoading = false, 
                        recipes = results, 
                        filteredRecipes = applyLocalFilter(results, it.activeFilter),
                        error = null 
                    ) 
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onFilterSelect(filter: String?) {
        _state.update { 
            val newFilter = if (it.activeFilter == filter) null else filter
            it.copy(
                activeFilter = newFilter,
                filteredRecipes = applyLocalFilter(it.recipes, newFilter)
            )
        }
    }

    private fun applyLocalFilter(recipes: List<Recipe>, filter: String?): List<Recipe> {
        if (filter == null) return recipes
        
        return when (filter) {
            "Pedas" -> recipes.filter { 
                it.name.contains("Spicy", ignoreCase = true) || 
                it.name.contains("Chili", ignoreCase = true) ||
                it.name.contains("Curry", ignoreCase = true) ||
                it.instructions?.contains("chili", ignoreCase = true) == true
            }
            "Dessert" -> recipes.filter { 
                it.category?.contains("Dessert", ignoreCase = true) == true 
            }
            "Sehat" -> recipes.filter { 
                it.category?.contains("Seafood", ignoreCase = true) == true ||
                it.category?.contains("Vegetarian", ignoreCase = true) == true ||
                it.category?.contains("Vegan", ignoreCase = true) == true
            }
            "Cepat Dibuat" -> recipes.filter { 
                // Simple heuristic: fewer instructions steps or shorter text might mean faster
                (it.instructions?.length ?: 1000) < 500 
            }
            "Murah" -> recipes.filter {
                // Simple heuristic: fewer ingredients might mean cheaper
                it.ingredients.size < 6
            }
            else -> recipes
        }
    }
}
