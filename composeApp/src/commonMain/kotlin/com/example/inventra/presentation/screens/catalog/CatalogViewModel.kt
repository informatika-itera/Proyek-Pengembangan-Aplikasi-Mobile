package com.example.inventra.presentation.screens.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventra.domain.model.Item
import com.example.inventra.domain.model.ItemCategory
import com.example.inventra.domain.usecase.SearchItemsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
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

class CatalogViewModel(
    private val searchItemsUseCase: SearchItemsUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow(ItemCategory.ALL)

    val uiState: StateFlow<CatalogUiState> = combine(
        _searchQuery,
        _selectedCategory
    ) { query, category ->
        query to category
    }.flatMapLatest { (query, category) ->
        searchItemsUseCase(query, if (category == ItemCategory.ALL) null else category)
    }.combine(_searchQuery) { items, query ->
        items to query
    }.combine(_selectedCategory) { (items, query), category ->
        if (items.isEmpty()) {
            CatalogUiState.Empty
        } else {
            CatalogUiState.Success(items, query, category)
        }
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
