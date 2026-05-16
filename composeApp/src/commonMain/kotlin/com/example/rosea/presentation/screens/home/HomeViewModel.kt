package com.example.rosea.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rosea.domain.model.Product
import com.example.rosea.domain.repository.ProductRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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

    init {
        // Otomatis mengisi database lokal dengan dummy data kosmetik saat pertama kali aplikasi dibuka
        prepopulateDummyProducts()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelect(category: String?) {
        _selectedCategory.value = category
    }

    fun onSortOrderChange(order: SortOrder) {
        _sortOrder.value = order
    }

    private fun prepopulateDummyProducts() {
        viewModelScope.launch {
            productRepository.getAllProducts().first().let { currentList ->
                if (currentList.isEmpty()) {
                    val dummyData = listOf(
                        Product(1, "Low pH Good Morning Gel Cleanser", "COSRX", "Pembersih wajah lembut dengan kadar pH rendah yang aman untuk kulit sensitif.", 145000.0, "Cleanser", "https://example.com/cosrx.jpg", 0, 0),
                        Product(2, "Supple Preparation Unscented Toner", "Klairs", "Toner esensial berhidrasi tinggi tanpa kandungan wewangian atau alkohol.", 290000.0, "Toner", "https://example.com/klairs.jpg", 0, 0),
                        Product(3, "Centella Calming Gel Cream", "iUNIK", "Pelembab bertekstur gel ringan yang sangat efektif menenangkan kulit berjerawat.", 195000.0, "Moisturizer", "https://example.com/iunik.jpg", 0, 0),
                        Product(4, "Daily UV Defense Sunscreen SPF 36", "Innisfree", "Sunscreen berbasis air yang memberikan proteksi harian tanpa efek white-cast.", 160000.0, "Sunscreen", "https://example.com/innisfree.jpg", 0, 0)
                    )
                    dummyData.forEach { productRepository.insertProduct(it) }
                }
            }
        }
    }
}

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val products: List<Product>) : HomeUiState
}

enum class SortOrder {
    NONE, PRICE_LOW_TO_HIGH, PRICE_HIGH_TO_LOW
}