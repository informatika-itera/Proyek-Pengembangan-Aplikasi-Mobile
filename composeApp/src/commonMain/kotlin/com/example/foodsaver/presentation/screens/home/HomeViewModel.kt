package com.example.foodsaver.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodsaver.domain.model.FoodItem
import com.example.foodsaver.domain.model.FoodStatus
import com.example.foodsaver.domain.usecase.DeleteFoodUseCase
import com.example.foodsaver.domain.usecase.GetAllFoodUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val items: List<FoodItem> = emptyList(),
    val filteredItems: List<FoodItem> = emptyList(),
    val priorityItems: List<FoodItem> = emptyList(),
    val searchQuery: String = "",
    val totalItems: Int = 0,
    val safeCount: Int = 0,
    val nearlyExpiredCount: Int = 0,
    val expiredCount: Int = 0,
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

    fun loadItems() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getAllFoodUseCase()
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { items ->
                    val safe = items.count { it.getStatus() == FoodStatus.SAFE }
                    val nearlyExpired = items.count { it.getStatus() == FoodStatus.NEAR_EXPIRY }
                    val expired = items.count { it.getStatus() == FoodStatus.EXPIRED }
                    
                    // Priority items are those nearly expired or already expired
                    val priority = items.filter { it.getStatus() != FoodStatus.SAFE }
                        .sortedBy { it.getDaysRemaining() }
                        .take(5)
                    
                    _state.update { it.copy(
                        isLoading = false, 
                        items = items, 
                        filteredItems = filterItems(items, it.searchQuery),
                        priorityItems = priority,
                        totalItems = items.size,
                        safeCount = safe,
                        nearlyExpiredCount = nearlyExpired,
                        expiredCount = expired,
                        error = null
                    ) }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { 
            it.copy(
                searchQuery = query,
                filteredItems = filterItems(it.items, query)
            )
        }
    }

    private fun filterItems(items: List<FoodItem>, query: String): List<FoodItem> {
        return if (query.isBlank()) {
            items
        } else {
            items.filter { 
                it.name.contains(query, ignoreCase = true) || 
                it.category.contains(query, ignoreCase = true) ||
                it.storageLocation.contains(query, ignoreCase = true) ||
                it.getStatusLabel().contains(query, ignoreCase = true)
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
