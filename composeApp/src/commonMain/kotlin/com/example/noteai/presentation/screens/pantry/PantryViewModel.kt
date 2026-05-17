package com.example.noteai.presentation.screens.pantry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteai.domain.model.PantryItem
import com.example.noteai.domain.usecase.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PantryViewModel(
    getPantryItems: GetPantryItems,
    private val addPantryItem: AddPantryItem,
    private val updatePantryItem: UpdatePantryItem,
    private val deletePantryItem: DeletePantryItem,
    private val updateStockAmount: UpdateStockAmount
) : ViewModel() {

    val pantryItems: StateFlow<List<PantryItem>> = getPantryItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
