package com.example.foodsaver.presentation.screens.expiry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodsaver.domain.model.FoodItem
import com.example.foodsaver.domain.model.FoodStatus
import com.example.foodsaver.domain.usecase.GetAllFoodUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ExpiryUiState(
    val items: List<FoodItem> = emptyList(),
    val isLoading: Boolean = false,
    val selectedTab: Int = 0 // 0: Semua, 1: Hampir Expired, 2: Expired
) {
    val filteredItems: List<FoodItem>
        get() = when (selectedTab) {
            1 -> items.filter { it.getStatus() == FoodStatus.NEAR_EXPIRY }
            2 -> items.filter { it.getStatus() == FoodStatus.EXPIRED }
            else -> items
        }.sortedBy { it.getDaysRemaining() }
}

class ExpiryViewModel(
    private val getAllFoodUseCase: GetAllFoodUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ExpiryUiState())
    val state: StateFlow<ExpiryUiState> = _state.asStateFlow()

    init {
        loadItems()
    }

    private fun loadItems() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getAllFoodUseCase().collect { items ->
                _state.update { it.copy(items = items, isLoading = false) }
            }
        }
    }

    fun onTabSelected(index: Int) {
        _state.update { it.copy(selectedTab = index) }
    }
}
