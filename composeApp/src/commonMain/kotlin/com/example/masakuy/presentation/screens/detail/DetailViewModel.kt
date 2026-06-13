package com.example.masakuy.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.masakuy.core.network.Result
import com.example.masakuy.domain.model.RecipeDetail
import com.example.masakuy.domain.repository.AIRepository
import com.example.masakuy.domain.repository.RecipeRepository
import com.example.masakuy.domain.usecase.SaveFavoriteUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DetailUiState(
    val recipe: RecipeDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class DetailViewModel(
    private val aiRepository: AIRepository,
    private val saveFavoriteUseCase: SaveFavoriteUseCase,
    private val recipeRepository: RecipeRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadRecipe(recipeId: String, recipeName: String, budget: Int) {
        viewModelScope.launch(dispatcher) {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            var dbData: RecipeDetail? = null
            try {
                recipeRepository.getRecipeById(recipeId).collect { result ->
                    if (result is Result.Success) dbData = result.data
                }
            } catch (e: Exception) {}

            val hasDetail = dbData != null &&
                dbData!!.ingredients.isNotEmpty() &&
                dbData!!.instructions.isNotEmpty()

            if (hasDetail) {
                _uiState.value = _uiState.value.copy(recipe = dbData, isLoading = false)
                return@launch
            }

            val nameToUse = dbData?.name?.takeIf { it.isNotBlank() } ?: recipeName
            val budgetToUse = if (budget > 0) budget else dbData?.estimatedCost ?: 15000

            aiRepository.getRecipeDetail(nameToUse, budgetToUse).collect { result ->
                when (result) {
                    is Result.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
                    is Result.Success -> {
                        val merged = result.data.copy(
                            id = dbData?.id ?: result.data.id,
                            isFavorite = dbData?.isFavorite ?: false
                        )
                        try { recipeRepository.insertRecipe(merged) } catch (e: Exception) {}
                        _uiState.value = _uiState.value.copy(recipe = merged, isLoading = false)
                    }
                    is Result.Error -> _uiState.value = _uiState.value.copy(
                        error = result.exception.message ?: "Terjadi kesalahan",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun toggleFavorite(recipeId: String, isFavorite: Boolean) {
        viewModelScope.launch(dispatcher) {
            saveFavoriteUseCase(recipeId, isFavorite)
            _uiState.value = _uiState.value.copy(
                recipe = _uiState.value.recipe?.copy(isFavorite = isFavorite)
            )
        }
    }
}