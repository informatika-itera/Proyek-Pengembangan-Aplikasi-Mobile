package com.example.tripmate.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripmate.domain.model.PackingItem
import com.example.tripmate.domain.repository.PackingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PackingViewModel(
    private val repository: PackingRepository
) : ViewModel() {

    private val _items = MutableStateFlow<List<PackingItem>>(emptyList())
    val items: StateFlow<List<PackingItem>> = _items.asStateFlow()

    fun loadItems(tripId: Long) {
        viewModelScope.launch {
            repository.getItemsByTripId(tripId).collect {
                _items.value = it
            }
        }
    }

    fun addItem(tripId: Long, name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.insertItem(
                PackingItem(tripId = tripId, name = name.trim())
            )
        }
    }

    fun toggleItem(id: Long, current: Boolean) {
        viewModelScope.launch {
            repository.updateChecked(id, !current)
        }
    }

    fun deleteItem(id: Long) {
        viewModelScope.launch {
            repository.deleteItem(id)
        }
    }
}
