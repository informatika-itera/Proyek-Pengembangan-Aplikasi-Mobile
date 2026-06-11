package com.example.foodsaver.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodsaver.data.local.datastore.UserPreferences
import com.example.foodsaver.domain.model.FoodItem
import com.example.foodsaver.domain.model.FoodStatus
import com.example.foodsaver.domain.usecase.DeleteFoodUseCase
import com.example.foodsaver.domain.usecase.GetAllFoodUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val items: List<FoodItem> = emptyList(),
    val activeItems: List<FoodItem> = emptyList(),
    val filteredItems: List<FoodItem> = emptyList(),
    val priorityItems: List<FoodItem> = emptyList(),
    val urgentReminders: List<FoodItem> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String = "Semua",
    val totalItems: Int = 0,
    val safeCount: Int = 0,
    val nearlyExpiredCount: Int = 0,
    val expiredCount: Int = 0,
    val notificationsEnabled: Boolean = true,
    val reminderDays: Int = 1,
    val error: String? = null
)

class HomeViewModel(
    private val getAllFoodUseCase: GetAllFoodUseCase,
    private val deleteFoodUseCase: DeleteFoodUseCase,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        observePreferences()
        loadItems()
    }

    private fun observePreferences() {
        viewModelScope.launch {
            combine(
                userPreferences.notificationsEnabled,
                userPreferences.reminderDays
            ) { enabled, days ->
                enabled to days
            }.collect { (enabled, days) ->
                _state.update { it.copy(notificationsEnabled = enabled, reminderDays = days) }
                updateReminders()
            }
        }
    }

    fun loadItems() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getAllFoodUseCase()
                .catch { e ->
                    _state.update { 
                        it.copy(
                            isLoading = false, 
                            error = mapErrorMessage(e)
                        ) 
                    }
                }
                .collect { allItems ->
                    val activeItems = allItems.filter { !it.isConsumed && !it.isDiscarded }
                        .sortedBy { it.getDaysRemaining() }

                    val safe = activeItems.count { it.getStatus() == FoodStatus.SAFE }
                    val nearlyExpired = activeItems.count { it.getStatus() == FoodStatus.NEAR_EXPIRY }
                    val expired = activeItems.count { it.getStatus() == FoodStatus.EXPIRED || it.getStatus() == FoodStatus.EXPIRED_TODAY }
                    
                    val priority = activeItems.filter { it.getStatus() != FoodStatus.SAFE }.take(5)
                    
                    _state.update { it.copy(
                        isLoading = false, 
                        items = allItems,
                        activeItems = activeItems,
                        filteredItems = filterItems(activeItems, it.searchQuery, it.selectedCategory),
                        priorityItems = priority,
                        totalItems = activeItems.size,
                        safeCount = safe,
                        nearlyExpiredCount = nearlyExpired,
                        expiredCount = expired,
                        error = null
                    ) }
                    updateReminders()
                }
        }
    }

    private fun updateReminders() {
        val currentState = _state.value
        if (!currentState.notificationsEnabled) {
            _state.update { it.copy(urgentReminders = emptyList()) }
            return
        }

        val reminders = currentState.activeItems.filter { item ->
            val days = item.getDaysRemaining()
            days == 0 || (days > 0 && days <= currentState.reminderDays)
        }
        _state.update { it.copy(urgentReminders = reminders) }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { 
            val filtered = filterItems(it.activeItems, query, it.selectedCategory)
            it.copy(searchQuery = query, filteredItems = filtered)
        }
    }

    fun onCategoryChange(category: String) {
        _state.update {
            val filtered = filterItems(it.activeItems, it.searchQuery, category)
            it.copy(selectedCategory = category, filteredItems = filtered)
        }
    }

    private fun filterItems(items: List<FoodItem>, query: String, category: String): List<FoodItem> {
        return items.filter { item ->
            val matchesQuery = query.isBlank() || 
                    item.name.contains(query, ignoreCase = true) || 
                    item.category.contains(query, ignoreCase = true) ||
                    item.storageLocation.contains(query, ignoreCase = true)
            
            val matchesCategory = category == "Semua" || item.category == category
            
            matchesQuery && matchesCategory
        }
    }

    fun deleteItem(id: Long) {
        viewModelScope.launch {
            try {
                deleteFoodUseCase(id)
            } catch (e: Exception) {
                _state.update { it.copy(error = "Gagal menghapus data. Silakan coba lagi.") }
            }
        }
    }

    private fun mapErrorMessage(throwable: Throwable): String {
        return when {
            throwable is kotlinx.serialization.SerializationException -> "Gagal memproses data stok."
            else -> "Terjadi kendala saat memuat data. Coba lagi ya!"
        }
    }
}
