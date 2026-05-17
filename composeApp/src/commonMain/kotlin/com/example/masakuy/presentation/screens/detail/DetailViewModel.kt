package com.example.masakuy.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.masakuy.core.network.Result
import com.example.masakuy.domain.model.RecipeDetail
import com.example.masakuy.domain.usecase.GetRecipeDetailUseCase
import com.example.masakuy.domain.usecase.SaveFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DetailUiState(
    val recipe: RecipeDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFavorite: Boolean = false
)

class DetailViewModel(
    private val getRecipeDetailUseCase: GetRecipeDetailUseCase,
    private val saveFavoriteUseCase: SaveFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadRecipe(recipeId: String) {
        viewModelScope.launch {
            getRecipeDetailUseCase(recipeId).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            recipe = result.data,
                            isLoading = false,
                            isFavorite = result.data.isFavorite
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            error = result.exception.message,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun toggleFavorite(recipeId: String) {
        viewModelScope.launch {
            try {
                val newFavoriteState = !_uiState.value.isFavorite
                saveFavoriteUseCase(recipeId, newFavoriteState)
                _uiState.value = _uiState.value.copy(isFavorite = newFavoriteState)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}