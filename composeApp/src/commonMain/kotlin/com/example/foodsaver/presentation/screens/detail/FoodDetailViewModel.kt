package com.example.foodsaver.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodsaver.domain.model.FoodItem
import com.example.foodsaver.domain.usecase.DeleteFoodUseCase
import com.example.foodsaver.domain.usecase.GetFoodDetailUseCase
import com.example.foodsaver.domain.usecase.SaveFoodUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FoodDetailUiState(
    val isLoading: Boolean = false,
    val foodItem: FoodItem? = null,
    val error: String? = null,
    val isDeleted: Boolean = false
)

class FoodDetailViewModel(
    private val getFoodDetailUseCase: GetFoodDetailUseCase,
    private val deleteFoodUseCase: DeleteFoodUseCase,
    private val saveFoodUseCase: SaveFoodUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FoodDetailUiState())
    val state: StateFlow<FoodDetailUiState> = _state.asStateFlow()

    fun loadFoodDetail(id: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val item = getFoodDetailUseCase(id)
                if (item != null) {
                    _state.update { it.copy(isLoading = false, foodItem = item) }
                } else {
                    _state.update { it.copy(isLoading = false, error = "Item tidak ditemukan") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun toggleConsumed() {
        val currentItem = _state.value.foodItem ?: return
        viewModelScope.launch {
            try {
                val updatedItem = currentItem.copy(isConsumed = !currentItem.isConsumed)
                saveFoodUseCase(updatedItem)
                _state.update { it.copy(foodItem = updatedItem) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun toggleDiscarded() {
        val currentItem = _state.value.foodItem ?: return
        viewModelScope.launch {
            try {
                val updatedItem = currentItem.copy(isDiscarded = !currentItem.isDiscarded)
                saveFoodUseCase(updatedItem)
                _state.update { it.copy(foodItem = updatedItem) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun deleteItem() {
        val id = _state.value.foodItem?.id ?: return
        viewModelScope.launch {
            try {
                deleteFoodUseCase(id)
                _state.update { it.copy(isDeleted = true) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }
}
