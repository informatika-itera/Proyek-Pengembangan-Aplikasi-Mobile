package com.example.foodsaver.presentation.screens.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodsaver.domain.model.*
import com.example.foodsaver.domain.repository.FoodRepository
import com.example.foodsaver.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class RecommendationUiState {
    object Idle : RecommendationUiState()
    object Loading : RecommendationUiState()
    data class Success(val recommendation: RecipeRecommendation) : RecommendationUiState()
    object Empty : RecommendationUiState()
    data class Error(val message: String) : RecommendationUiState()
    data class Fallback(val recommendation: RecipeRecommendation) : RecommendationUiState()
}

sealed class CookFromStockEvent {
    object NavigateToResult : CookFromStockEvent()
}

data class CookFromStockUiState(
    val isLoadingIngredients: Boolean = false,
    val ingredients: List<FoodItem> = emptyList(),
    val selectedIngredientIds: Set<Long> = emptySet(),
    val manualIngredients: List<String> = emptyList(),
    val prioritizeExpired: Boolean = true,
    val preference: String = "Praktis",
    val recommendationState: RecommendationUiState = RecommendationUiState.Idle,
    val validationError: String? = null
)

class CookFromStockViewModel(
    private val foodRepository: FoodRepository,
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CookFromStockUiState())
    val state: StateFlow<CookFromStockUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<CookFromStockEvent>()
    val events: SharedFlow<CookFromStockEvent> = _events.asSharedFlow()

    init {
        loadIngredients()
    }

    private fun loadIngredients() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingIngredients = true) }
            foodRepository.getAllFoodItems()
                .map { items -> 
                    items.filter { !it.isConsumed && !it.isDiscarded }
                        .sortedBy { it.getDaysRemaining() }
                }
                .collect { items ->
                    _state.update { it.copy(isLoadingIngredients = false, ingredients = items) }
                }
        }
    }

    fun toggleIngredientSelection(id: Long) {
        _state.update { currentState ->
            val newSelection = if (currentState.selectedIngredientIds.contains(id)) {
                currentState.selectedIngredientIds - id
            } else {
                currentState.selectedIngredientIds + id
            }
            currentState.copy(selectedIngredientIds = newSelection)
        }
    }

    fun addManualIngredient(input: String) {
        if (input.isBlank()) return
        
        val newItems = input.split(",")
            .map { it.trim().lowercase() }
            .filter { it.isNotEmpty() }
        
        _state.update { currentState ->
            val currentList = currentState.manualIngredients.toMutableList()
            newItems.forEach { item ->
                if (!currentList.contains(item)) {
                    currentList.add(item)
                }
            }
            currentState.copy(manualIngredients = currentList, validationError = null)
        }
    }

    fun removeManualIngredient(ingredient: String) {
        _state.update { currentState ->
            currentState.copy(manualIngredients = currentState.manualIngredients - ingredient)
        }
    }

    fun setPrioritizeExpired(prioritize: Boolean) {
        _state.update { it.copy(prioritizeExpired = prioritize) }
    }

    fun setPreference(preference: String) {
        _state.update { it.copy(preference = preference) }
    }

    fun resetIngredients() {
        _state.update { it.copy(
            selectedIngredientIds = emptySet(),
            manualIngredients = emptyList(),
            recommendationState = RecommendationUiState.Idle,
            validationError = null
        ) }
    }

    fun resetRecommendationState() {
        _state.update { it.copy(recommendationState = RecommendationUiState.Idle) }
    }
    
    fun clearValidationError() {
        _state.update { it.copy(validationError = null) }
    }

    fun generateRecommendation(
        ingredientIds: List<Long>,
        manualIngredients: List<String>,
        prioritize: Boolean,
        pref: String
    ) {
        if (ingredientIds.isEmpty() && manualIngredients.isEmpty()) {
            _state.update { it.copy(validationError = "Pilih atau masukkan minimal satu bahan terlebih dahulu.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(
                recommendationState = RecommendationUiState.Loading,
                validationError = null
            ) }
            
            try {
                val currentIngredients = if (_state.value.ingredients.isEmpty()) {
                    foodRepository.getAllFoodItems()
                        .map { it.filter { f -> !f.isConsumed && !f.isDiscarded } }
                        .first()
                } else {
                    _state.value.ingredients
                }
                
                val inventoryItems = currentIngredients.filter { ingredientIds.contains(it.id) }
                
                val recipeIngredients = inventoryItems.map { 
                    RecipeIngredient(
                        name = it.name,
                        quantity = "${it.quantity} ${it.unit}",
                        source = IngredientSource.INVENTORY,
                        expiryStatus = it.getStatus(),
                        daysLeft = it.getDaysRemaining()
                    )
                } + manualIngredients.map { 
                    RecipeIngredient(
                        name = it,
                        quantity = "Secukupnya",
                        source = IngredientSource.MANUAL
                    )
                }

                val result = recipeRepository.getRecommendations(recipeIngredients, pref, prioritize)
                
                result.fold(
                    onSuccess = { recommendation ->
                        val isFallback = recommendation.description.contains("Lokal") || 
                                         recommendation.reason.contains("lokal", ignoreCase = true) ||
                                         recommendation.warningMessage?.contains("lokal", ignoreCase = true) == true
                        
                        _state.update { 
                            if (isFallback) {
                                it.copy(recommendationState = RecommendationUiState.Fallback(recommendation))
                            } else {
                                it.copy(recommendationState = RecommendationUiState.Success(recommendation))
                            }
                        }
                    },
                    onFailure = { e ->
                        _state.update { it.copy(
                            recommendationState = RecommendationUiState.Error(e.message ?: "Resep belum bisa dimuat. Periksa koneksi internet kamu lalu coba lagi.")
                        ) }
                    }
                )
            } catch (e: Exception) {
                _state.update { it.copy(
                    recommendationState = RecommendationUiState.Error("Terjadi kesalahan sistem. Silakan coba lagi nanti.")
                ) }
            } finally {
                // Once we have a result (Success/Fallback/Error/Empty), we navigate.
                // We only navigate if the state is NOT Idle or Loading.
                val currentState = _state.value.recommendationState
                if (currentState !is RecommendationUiState.Idle && currentState !is RecommendationUiState.Loading) {
                    _events.emit(CookFromStockEvent.NavigateToResult)
                }
            }
        }
    }

    fun markIngredientsAsUsed(ids: List<Long>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            ids.forEach { id ->
                foodRepository.getFoodItemById(id)?.let { item ->
                    foodRepository.updateFoodItem(item.copy(isConsumed = true))
                }
            }
            onSuccess()
        }
    }
}
