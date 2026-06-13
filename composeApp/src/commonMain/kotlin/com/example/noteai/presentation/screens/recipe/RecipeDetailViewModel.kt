package com.example.noteai.presentation.screens.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteai.domain.model.Recipe
import com.example.noteai.domain.usecase.GetRecipeById
import com.example.noteai.domain.usecase.DeleteRecipe
import com.example.noteai.domain.usecase.ToggleFavoriteRecipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RecipeDetailViewModel(
    private val getRecipeById: GetRecipeById,
    private val deleteRecipe: DeleteRecipe,
    private val toggleFavoriteRecipe: ToggleFavoriteRecipe
) : ViewModel() {

    private val _recipe = MutableStateFlow<Recipe?>(null)
    val recipe: StateFlow<Recipe?> = _recipe.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadRecipe(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            getRecipeById(id).collectLatest { recipe ->
                _recipe.value = recipe
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite(id: Long) {
        viewModelScope.launch {
            toggleFavoriteRecipe(id)
        }
    }

    fun removeRecipe(id: Long, onDeleted: () -> Unit) {
        viewModelScope.launch {
            deleteRecipe(id)
            onDeleted()
        }
    }
}
