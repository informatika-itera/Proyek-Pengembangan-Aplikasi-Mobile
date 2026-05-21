package com.example.foodsaver.presentation.screens.recipe.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodsaver.domain.model.MealPlan
import com.example.foodsaver.domain.model.MealType
import com.example.foodsaver.domain.model.Recipe
import com.example.foodsaver.domain.usecase.AddMealPlanUseCase
import com.example.foodsaver.domain.usecase.GetRecipeDetailsUseCase
import com.example.foodsaver.domain.usecase.ToggleFavoriteRecipeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

data class RecipeDetailUiState(
    val isLoading: Boolean = false,
    val recipe: Recipe? = null,
    val error: String? = null,
    val isMealPlanSaved: Boolean = false
)

class RecipeDetailViewModel(
    private val getRecipeDetailsUseCase: GetRecipeDetailsUseCase,
    private val toggleFavoriteRecipeUseCase: ToggleFavoriteRecipeUseCase,
    private val addMealPlanUseCase: AddMealPlanUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RecipeDetailUiState())
    val state: StateFlow<RecipeDetailUiState> = _state.asStateFlow()

    fun loadRecipe(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val result = getRecipeDetailsUseCase(id)
                _state.update { it.copy(isLoading = false, recipe = result, error = null) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun toggleFavorite() {
        val currentRecipe = _state.value.recipe ?: return
        viewModelScope.launch {
            try {
                toggleFavoriteRecipeUseCase(currentRecipe)
                val updatedRecipe = currentRecipe.copy(isFavorite = !currentRecipe.isFavorite)
                _state.update { it.copy(recipe = updatedRecipe) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun addToMealPlan(date: LocalDate, mealType: MealType) {
        val recipe = _state.value.recipe ?: return
        viewModelScope.launch {
            try {
                addMealPlanUseCase(
                    MealPlan(
                        recipeId = recipe.id,
                        recipeName = recipe.name,
                        recipeImageUrl = recipe.imageUrl,
                        date = date,
                        mealType = mealType
                    )
                )
                _state.update { it.copy(isMealPlanSaved = true) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun resetMealPlanStatus() {
        _state.update { it.copy(isMealPlanSaved = false) }
    }
}
