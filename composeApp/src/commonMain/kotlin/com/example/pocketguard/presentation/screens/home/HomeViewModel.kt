package com.example.pocketguard.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketguard.domain.model.Transaction
import com.example.pocketguard.domain.model.TransactionCategory
import com.example.pocketguard.domain.usecase.DeleteTransactionUseCase
import com.example.pocketguard.domain.usecase.GetAllTransactionsUseCase
import com.example.pocketguard.domain.usecase.TransactionSortBy
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getAllTransactionsUseCase: GetAllTransactionsUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase
) : ViewModel() {

    private val _query = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow<TransactionCategory?>(null)
    private val _sortBy = MutableStateFlow(TransactionSortBy.DATE_DESC)
    val sortBy: StateFlow<TransactionSortBy> = _sortBy.asStateFlow()

    // Menggabungkan aliran data (Search, Filter, Sort, dan Database)
    val uiState: StateFlow<HomeUiState> = combine(
        _query,
        _selectedCategory,
        _sortBy,
        getAllTransactionsUseCase()
    ) { query, category, sort, transactions ->
        // 1. Filter berdasarkan pencarian deskripsi
        val filteredByQuery = if (query.isBlank()) {
            transactions
        } else {
            transactions.filter { it.description.contains(query, ignoreCase = true) }
        }

        // 2. Filter berdasarkan kategori
        val filteredByCategory = if (category == null) {
            filteredByQuery
        } else {
            filteredByQuery.filter { it.category == category }
        }

        // 3. Urutkan data (Logika sorting sudah ada di UseCase, tapi bisa dipertegas di sini)
        val sortedTransactions = when (sort) {
            TransactionSortBy.DATE_ASC -> filteredByCategory.sortedBy { it.createdAt }
            TransactionSortBy.DATE_DESC -> filteredByCategory.sortedByDescending { it.createdAt }
            TransactionSortBy.AMOUNT_ASC -> filteredByCategory.sortedBy { it.amount }
            TransactionSortBy.AMOUNT_DESC -> filteredByCategory.sortedByDescending { it.amount }
            TransactionSortBy.CATEGORY -> filteredByCategory.sortedBy { it.category.name }
        }

        if (sortedTransactions.isEmpty()) {
            HomeUiState.Empty(query, category)
        } else {
            HomeUiState.Success(sortedTransactions, query, category)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState.Loading
    )

    // ==================== USER ACTIONS ====================

    fun onSearchQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    fun clearSearch() {
        _query.value = ""
        _selectedCategory.value = null
    }

    fun onCategorySelected(category: TransactionCategory?) {
        _selectedCategory.value = category
    }

    fun onSortByChanged(sort: TransactionSortBy) {
        _sortBy.value = sort
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            deleteTransactionUseCase(id)
        }
    }
}

// ==================== UI STATE MODELS ====================

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Success(
        val transactions: List<Transaction>,
        val query: String,
        val category: TransactionCategory?
    ) : HomeUiState

    data class Empty(
        val query: String,
        val category: TransactionCategory?
    ) : HomeUiState

    data class Error(val message: String) : HomeUiState
}