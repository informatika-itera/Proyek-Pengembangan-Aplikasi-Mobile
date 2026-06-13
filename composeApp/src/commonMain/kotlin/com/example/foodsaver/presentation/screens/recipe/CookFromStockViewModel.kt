package com.example.foodsaver.presentation.screens.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodsaver.domain.model.*
import com.example.foodsaver.domain.repository.FoodRepository
import com.example.foodsaver.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class RecommendationUiState {
    data object Idle : RecommendationUiState()
    data object Loading : RecommendationUiState()
    data class Success(val recommendation: RecipeRecommendation) : RecommendationUiState()
    data object Empty : RecommendationUiState()
    data class Error(val message: String) : RecommendationUiState()
    data class Fallback(val recommendation: RecipeRecommendation) : RecommendationUiState()
}

sealed class CookFromStockEvent {
    data object NavigateToResult : CookFromStockEvent()
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
                .catch { e ->
                    _state.update { it.copy(isLoadingIngredients = false, validationError = "Gagal memuat bahan dari stok.") }
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
                val inventoryItems = _state.value.ingredients.filter { ingredientIds.contains(it.id) }
                
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

                recipeRepository.getRecommendations(recipeIngredients, pref, prioritize)
                    .onSuccess { recommendation ->
                        val isFallback = recommendation.description.contains("Lokal") || 
                                         recommendation.reason.contains("lokal", ignoreCase = true)
                        
                        _state.update { 
                            if (isFallback) {
                                it.copy(recommendationState = RecommendationUiState.Fallback(recommendation))
                            } else {
                                it.copy(recommendationState = RecommendationUiState.Success(recommendation))
                            }
                        }
                        _events.emit(CookFromStockEvent.NavigateToResult)
                    }
                    .onFailure { e ->
                        _state.update { it.copy(
                            recommendationState = RecommendationUiState.Error(mapErrorMessage(e))
                        ) }
                    }
            } catch (e: Exception) {
                _state.update { it.copy(
                    recommendationState = RecommendationUiState.Error("Maaf, terjadi kesalahan saat mencari resep.")
                ) }
            }
        }
    }

    private fun mapErrorMessage(e: Throwable): String {
        return when {
            e.message?.contains("internet", ignoreCase = true) == true -> "Koneksi bermasalah. Coba lagi nanti."
            else -> "Resep belum ditemukan. Coba kombinasi bahan lain."
        }
    }

    fun markIngredientsAsUsed(ids: List<Long>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                ids.forEach { id ->
                    foodRepository.getFoodItemById(id)?.let { item ->
                        foodRepository.updateFoodItem(item.copy(isConsumed = true))
                    }
                }
                onSuccess()
            } catch (e: Exception) {
                // Silently fail or log
            }
        }
    }
}
