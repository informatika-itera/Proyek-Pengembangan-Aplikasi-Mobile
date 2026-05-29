package com.example.noteai.presentation.screens.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteai.domain.model.Recipe
import com.example.noteai.domain.usecase.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecipeViewModel(
    getRecipes: GetRecipes,
    private val addRecipe: AddRecipe,
    private val updateRecipe: UpdateRecipe,
    private val deleteRecipe: DeleteRecipe,
    private val toggleFavoriteRecipe: ToggleFavoriteRecipe
) : ViewModel() {

    // State untuk mengetahui Tab mana yang aktif
    private val _showFavoritesOnly = MutableStateFlow(false)
    val showFavoritesOnly: StateFlow<Boolean> = _showFavoritesOnly.asStateFlow()

    // Menggabungkan data dari local DB dengan filter yang dipilih
    val recipes: StateFlow<List<Recipe>> = combine(
        getRecipes(),
        _showFavoritesOnly
    ) { recipeList, isFavoritesOnly ->
        if (isFavoritesOnly) {
            recipeList.filter { it.isFavorite }
        } else {
            recipeList
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setShowFavoritesOnly(show: Boolean) {
        _showFavoritesOnly.value = show
    }

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