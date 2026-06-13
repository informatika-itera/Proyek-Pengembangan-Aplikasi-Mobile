package com.example.rosea.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rosea.domain.model.Product
import com.example.rosea.domain.repository.ProductRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _selectedTab = MutableStateFlow("Latest")
    val selectedTab = _selectedTab.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.NONE)
    val sortOrder = _sortOrder.asStateFlow()

    // Menggabungkan State secara reaktif (Memenuhi spesifikasi Sprint 3)
    val uiState: StateFlow<HomeUiState> = combine(
        _searchQuery
            .debounce(300L) // Fitur Lanjutan: Debounce menahan panggilan selama 300ms
            .distinctUntilChanged(),
        _selectedCategory,
        _selectedTab,
        _sortOrder
    ) { query, category, tab, sort ->
        DataState(query, category, tab, sort)
    }.flatMapLatest { state ->
        val productFlow = when {
            !state.category.isNullOrBlank() -> productRepository.getProductsByCategory(state.category)
            state.query.isNotBlank() -> productRepository.searchProducts(state.query)
            else -> productRepository.getAllProducts()
        }

        productFlow.map { products ->
            var processedList = products

            // Logika Tab (Dummy Logic for demo)
            processedList = when (state.tab) {
                "Popular" -> processedList.sortedByDescending { it.id % 3 == 0L } // Dummy: id kelipatan 3 dianggap populer
                "Promo" -> processedList.filter { it.price < 200000 } // Dummy: harga < 200rb dianggap promo
                else -> processedList.sortedByDescending { it.createdAt } // Latest
            }

            // Logika Sort manual jika ada
            processedList = when (state.sort) {
                SortOrder.PRICE_LOW_TO_HIGH -> processedList.sortedBy { it.price }
                SortOrder.PRICE_HIGH_TO_LOW -> processedList.sortedByDescending { it.price }
                SortOrder.NONE -> processedList
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

    fun onTabSelect(tab: String) {
        _selectedTab.value = tab
    }

    fun onSortOrderChange(order: SortOrder) {
        _sortOrder.value = order
    }

    private data class DataState(
        val query: String,
        val category: String?,
        val tab: String,
        val sort: SortOrder
    )
}

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val products: List<Product>) : HomeUiState
}

enum class SortOrder {
    NONE, PRICE_LOW_TO_HIGH, PRICE_HIGH_TO_LOW
}
