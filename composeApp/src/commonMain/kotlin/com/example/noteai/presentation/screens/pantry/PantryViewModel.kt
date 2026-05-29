package com.example.noteai.presentation.screens.pantry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteai.domain.model.PantryItem
import com.example.noteai.domain.usecase.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class PantryViewModel(
    getPantryItems: GetPantryItems,
    private val addPantryItem: AddPantryItem,
    private val updatePantryItem: UpdatePantryItem,
    private val deletePantryItem: DeletePantryItem,
    private val updateStockAmount: UpdateStockAmount
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    // Combine item, query search (dengan debounce 300ms), dan kategori terpilih
    val pantryItems: StateFlow<List<PantryItem>> = combine(
        getPantryItems(),
        _searchQuery.debounce(300),
        _selectedCategory
    ) { items, query, category ->
        items.filter { item ->
            val matchesQuery = item.name.contains(query, ignoreCase = true)
            val matchesCategory = category == null || item.category == category
            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Actions untuk UI
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    fun onCategorySelected(category: String?) {
        _selectedCategory.value = category
    }

    // Use cases Actions
    fun addItem(name: String, amount: Double, unit: String, category: String, minStock: Double) {
        viewModelScope.launch {
            addPantryItem(PantryItem(name = name, amount = amount, unit = unit, category = category, minStock = minStock))
        }
    }

    fun updateItem(item: PantryItem) {
        viewModelScope.launch {
            updatePantryItem(item)
        }
    }

    fun removeItem(id: Long) {
        viewModelScope.launch {
            deletePantryItem(id)
        }
    }

    fun updateAmount(id: Long, currentAmount: Double, delta: Double) {
        viewModelScope.launch {
            val newAmount = (currentAmount + delta).coerceAtLeast(0.0)
            updateStockAmount(id, newAmount)
        }
    }
}