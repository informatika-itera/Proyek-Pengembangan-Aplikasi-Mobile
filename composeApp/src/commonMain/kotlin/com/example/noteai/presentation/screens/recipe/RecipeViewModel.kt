package com.example.noteai.presentation.screens.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteai.domain.model.Recipe
import com.example.noteai.domain.usecase.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecipeViewModel(
    getRecipes: GetRecipes,
    private val addRecipe: AddRecipe,
    private val updateRecipe: UpdateRecipe,
    private val deleteRecipe: DeleteRecipe,
    private val toggleFavoriteRecipe: ToggleFavoriteRecipe
) : ViewModel() {

    val recipes: StateFlow<List<Recipe>> = getRecipes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveRecipe(recipe: Recipe) {
        viewModelScope.launch {
            if (recipe.id == 0L) {
                addRecipe(recipe)
            } else {
                updateRecipe(recipe)
            }
        }
    }

    fun removeRecipe(id: Long) {
        viewModelScope.launch {
            deleteRecipe(id)
        }
    }

    fun toggleFavorite(id: Long) {
        viewModelScope.launch {
            toggleFavoriteRecipe(id)
        }
    }
}
