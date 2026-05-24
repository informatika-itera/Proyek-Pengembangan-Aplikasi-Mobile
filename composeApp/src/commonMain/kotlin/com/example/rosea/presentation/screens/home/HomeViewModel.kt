package com.example.rosea.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rosea.domain.model.Product
import com.example.rosea.domain.repository.ProductRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

@OptIn(FlowPreview::class)
class HomeViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.NONE)
    val sortOrder = _sortOrder.asStateFlow()

    // Menggabungkan State secara reaktif (Memenuhi spesifikasi Sprint 3)
    val uiState: StateFlow<HomeUiState> = combine(
        _searchQuery
            .debounce(300L) // Fitur Lanjutan: Debounce menahan panggilan selama 300ms
            .distinctUntilChanged(),
        _selectedCategory,
        _sortOrder
    ) { query, category, sort ->
        Triple(query, category, sort)
    }.flatMapLatest { (query, category, sort) ->
        val productFlow = when {
            !category.isNullOrBlank() -> productRepository.getProductsByCategory(category)
            query.isNotBlank() -> productRepository.searchProducts(query)
            else -> productRepository.getAllProducts()
        }

        productFlow.map { products ->
            val processedList = when (sort) {
                SortOrder.PRICE_LOW_TO_HIGH -> products.sortedBy { it.price }
                SortOrder.PRICE_HIGH_TO_LOW -> products.sortedByDescending { it.price }
                SortOrder.NONE -> products
            }
            HomeUiState.Success(processedList)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState.Loading
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelect(category: String?) {
        _selectedCategory.value = category
    }

    fun onSortOrderChange(order: SortOrder) {
        _sortOrder.value = order
    }
}

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val products: List<Product>) : HomeUiState
}

enum class SortOrder {
    NONE, PRICE_LOW_TO_HIGH, PRICE_HIGH_TO_LOW
}