package com.example.inventra.presentation.screens.catalog

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventra.domain.model.Item
import com.example.inventra.domain.model.ItemCategory
import com.example.inventra.domain.model.User
import com.example.inventra.domain.repository.AuthRepository
import com.example.inventra.domain.repository.ItemRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

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
    private val itemRepository: ItemRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow(TextFieldValue(""))
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(ItemCategory.ALL)

    val currentUser: StateFlow<User?> = flow {
        emit(authRepository.getCurrentUser())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val uiState: StateFlow<CatalogUiState> = combine(
        // Debounce 300ms — API tidak dipanggil setiap ketikan
        _searchQuery.map { it.text }.debounce(300L),
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
        val query = _searchQuery.value.text
        val category = _selectedCategory.value
        if (items.isEmpty()) CatalogUiState.Empty
        else CatalogUiState.Success(items, query, category)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CatalogUiState.Loading
    )

    fun onSearchQueryChange(query: TextFieldValue) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: ItemCategory) {
        _selectedCategory.value = category
    }
}