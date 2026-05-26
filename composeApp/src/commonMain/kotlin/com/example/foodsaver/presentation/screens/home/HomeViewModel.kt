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
    val items: List<FoodItem> = emptyList(), // All items
    val activeItems: List<FoodItem> = emptyList(), // Not consumed, not discarded
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
                .collect { allItems ->
                    // Logic: Only show items that are NOT consumed and NOT discarded
                    val activeItems = allItems.filter { !it.isConsumed && !it.isDiscarded }
                        .sortedBy { it.getDaysRemaining() }

                    val safe = activeItems.count { it.getStatus() == FoodStatus.SAFE }
                    val nearlyExpired = activeItems.count { it.getStatus() == FoodStatus.NEAR_EXPIRY }
                    val expired = activeItems.count { it.getStatus() == FoodStatus.EXPIRED || it.getStatus() == FoodStatus.EXPIRED_TODAY }
                    
                    // Priority items are those nearly expired or already expired, limit to 5
                    val priority = activeItems.filter { it.getStatus() != FoodStatus.SAFE }
                        .take(5)
                    
                    _state.update { it.copy(
                        isLoading = false, 
                        items = allItems,
                        activeItems = activeItems,
                        filteredItems = filterItems(activeItems, it.searchQuery),
                        priorityItems = priority,
                        totalItems = activeItems.size,
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
                filteredItems = filterItems(it.activeItems, query)
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

    fun deleteItem(id: Long) {
        viewModelScope.launch {
            deleteFoodUseCase(id)
        }
    }
}
