package com.mywallet.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mywallet.domain.model.Transaction
import com.mywallet.domain.repository.TransactionRepository
import com.mywallet.domain.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: TransactionRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val userName: StateFlow<String> = userRepository.profileState
        .map { it.name.split(" ").firstOrNull() ?: "User" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "User")

    private val _allTransactions = MutableStateFlow<List<Transaction>>(emptyList())

    init {
        loadTransactions()
    }

    fun loadTransactions() {
        viewModelScope.launch {
            repository.getAllTransactions()
                .catch { e -> _uiState.value = HomeUiState.Error(e.message ?: "Terjadi kesalahan") }
                .collect { transactions ->
                    _allTransactions.value = transactions
                    updateFilteredState()
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        updateFilteredState()
    }

    private fun updateFilteredState() {
        val transactions = _allTransactions.value
        val query = _searchQuery.value

        val filteredTransactions = if (query.isBlank()) {
            transactions
        } else {
            transactions.filter { 
                it.title.contains(query, ignoreCase = true) || 
                it.amount.toString().contains(query)
            }
        }

        if (filteredTransactions.isEmpty() && query.isBlank()) {
            _uiState.value = HomeUiState.Empty
        } else {
            val income = _allTransactions.value.filter { it.type.name == "INCOME" }.sumOf { it.amount }
            val expense = _allTransactions.value.filter { it.type.name == "EXPENSE" }.sumOf { it.amount }
            _uiState.value = HomeUiState.Success(
                transactions = filteredTransactions,
                balance = income - expense,
                totalIncome = income,
                totalExpense = expense
            )
        }
    }

    fun deleteTransaction(id: Int) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
        }
    }
}
