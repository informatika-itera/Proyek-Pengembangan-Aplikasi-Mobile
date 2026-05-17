package com.example.masakuy.presentation.screens.recommendation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.masakuy.core.network.Result
import com.example.masakuy.domain.model.Recipe
import com.example.masakuy.domain.usecase.GetRecommendationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RecommendationUiState(
    val recipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedIngredients: List<String> = emptyList()
)

class RecommendationViewModel(
    private val getRecommendationUseCase: GetRecommendationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecommendationUiState())
    val uiState: StateFlow<RecommendationUiState> = _uiState.asStateFlow()

    fun getRecommendations(budget: Int) {
        viewModelScope.launch {
            getRecommendationUseCase(
                budget = budget,
                ingredients = _uiState.value.selectedIngredients
            ).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            recipes = result.data,
                            isLoading = false
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

    fun toggleIngredient(ingredient: String) {
        val current = _uiState.value.selectedIngredients.toMutableList()
        if (current.contains(ingredient)) {
            current.remove(ingredient)
        } else {
            current.add(ingredient)
        }
        _uiState.value = _uiState.value.copy(selectedIngredients = current)
    }
}