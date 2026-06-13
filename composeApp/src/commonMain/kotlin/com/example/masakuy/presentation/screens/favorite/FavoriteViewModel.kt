package com.example.masakuy.presentation.screens.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.masakuy.core.network.Result
import com.example.masakuy.domain.model.Recipe
import com.example.masakuy.domain.usecase.GetRecipesUseCase
import com.example.masakuy.domain.usecase.SaveFavoriteUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FavoriteUiState(
    val favorites: List<Recipe> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class FavoriteViewModel(
    private val getRecipesUseCase: GetRecipesUseCase,
    private val saveFavoriteUseCase: SaveFavoriteUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoriteUiState())
    val uiState: StateFlow<FavoriteUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch(dispatcher) {
            getRecipesUseCase().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                    is Result.Success -> {
                        val favoriteRecipes = result.data
                            .filter { it.isFavorite }
                            .reversed()

                        _uiState.value = _uiState.value.copy(
                            favorites = favoriteRecipes,
                            isLoading = false,
                            error = null
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            error = result.exception.message ?: "Terjadi kesalahan",
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun removeFavorite(recipeId: String) {
        viewModelScope.launch(dispatcher) {
            saveFavoriteUseCase(recipeId, false)
            _uiState.value = _uiState.value.copy(
                favorites = _uiState.value.favorites.filter { it.id != recipeId }
            )
        }
    }
}
