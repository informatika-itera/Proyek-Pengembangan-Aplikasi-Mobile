package com.example.foodsaver.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodsaver.domain.model.FoodItem
import com.example.foodsaver.domain.usecase.DeleteFoodUseCase
import com.example.foodsaver.domain.usecase.GetAllFoodUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val items: List<FoodItem> = emptyList(),
    val selectedIds: Set<Long> = emptySet(),
    val error: String? = null
)

class HomeViewModel(
    private val getAllFoodUseCase: GetAllFoodUseCase,
    private val deleteFoodUseCase: DeleteFoodUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        loadItems()
    }

    private fun loadItems() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getAllFoodUseCase()
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { items ->
                    _state.update { it.copy(isLoading = false, items = items, error = null) }
                }
        }
    }

    fun toggleSelection(id: Long) {
        _state.update { currentState ->
            val newSelection = if (currentState.selectedIds.contains(id)) {
                currentState.selectedIds - id
            } else {
                currentState.selectedIds + id
            }
            currentState.copy(selectedIds = newSelection)
        }
    }

    fun clearSelection() {
        _state.update { it.copy(selectedIds = emptySet()) }
    }

    fun deleteItem(id: Long) {
        viewModelScope.launch {
            deleteFoodUseCase(id)
        }
    }
}
