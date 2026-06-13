package com.example.masakuy.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.masakuy.core.network.Result
import com.example.masakuy.domain.model.Recipe
import com.example.masakuy.domain.usecase.GetRecipesUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val recipes: List<Recipe> = emptyList(),
    val favorites: List<Recipe> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class HomeViewModel(
    private val getRecipesUseCase: GetRecipesUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init { loadRecipes() }

    fun loadRecipes() {
        viewModelScope.launch(dispatcher) {
            getRecipesUseCase().collect { result ->
                when (result) {
                    is Result.Loading -> _uiState.value = _uiState.value.copy(
                        isLoading = true
                    )
                    is Result.Success -> _uiState.value = _uiState.value.copy(
                        recipes = result.data,
                        favorites = result.data.filter { it.isFavorite },
                        isLoading = false
                    )
                    is Result.Error -> _uiState.value = _uiState.value.copy(
                        error = result.exception.message ?: "Terjadi kesalahan",
                        isLoading = false
                    )
                }
            }
        }
    }
}