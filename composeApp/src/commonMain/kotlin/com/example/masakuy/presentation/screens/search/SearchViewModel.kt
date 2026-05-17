package com.example.masakuy.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.masakuy.core.network.Result
import com.example.masakuy.domain.model.Recipe
import com.example.masakuy.domain.usecase.GetRecipesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val results: List<Recipe> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedBudget: Int? = null
)

class SearchViewModel(
    private val getRecipesUseCase: GetRecipesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun updateQuery(newQuery: String) {
        _uiState.value = _uiState.value.copy(query = newQuery)
        if (newQuery.isNotEmpty()) {
            search(newQuery)
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                getRecipesUseCase().collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            _uiState.value = _uiState.value.copy(isLoading = true)
                        }
                        is Result.Success -> {
                            val filtered = result.data.filter { recipe ->
                                recipe.name.contains(query, ignoreCase = true) &&
                                        (
                                                _uiState.value.selectedBudget == null ||
                                                        recipe.estimatedCost <= _uiState.value.selectedBudget!!
                                                )
                            }
                            _uiState.value = _uiState.value.copy(
                                results = filtered,
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
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }

    fun filterByBudget(budget: Int?) {
        _uiState.value = _uiState.value.copy(selectedBudget = budget)
        if (_uiState.value.query.isNotEmpty()) {
            search(_uiState.value.query)
        }
    }
}