package com.example.inventra.presentation.screens.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventra.domain.model.Item
import com.example.inventra.domain.model.ItemCategory
import com.example.inventra.domain.repository.ItemRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed interface CatalogUiState {
    data object Loading : CatalogUiState
    data class Success(
        val items: List<Item>,
        val query: String,
        val selectedCategory: ItemCategory
    ) : CatalogUiState
    data object Empty : CatalogUiState
    data class Error(val message: String) : CatalogUiState
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class CatalogViewModel(
    private val itemRepository: ItemRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow(ItemCategory.ALL)

    val uiState: StateFlow<CatalogUiState> = combine(
        // Debounce 300ms — API tidak dipanggil setiap ketikan
        _searchQuery.debounce(300L),
        _selectedCategory
    ) { query, category ->
        query to category
    }.flatMapLatest { (query, category) ->
        if (query.isBlank()) {
            itemRepository.getItemsByCategory(category)
        } else {
            itemRepository.searchItems(query).map { items ->
                if (category != ItemCategory.ALL) {
                    items.filter { it.category == category }
                } else {
                    items
                }
            }
        }
    }.map { items ->
        val query = _searchQuery.value
        val category = _selectedCategory.value
        if (items.isEmpty()) CatalogUiState.Empty
        else CatalogUiState.Success(items, query, category)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CatalogUiState.Loading
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: ItemCategory) {
        _selectedCategory.value = category
    }
}