package com.mywallet.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mywallet.domain.model.Transaction
import com.mywallet.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: TransactionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadTransactions()
    }

    fun loadTransactions() {
        viewModelScope.launch {
            repository.getAllTransactions()
                .catch { e -> _uiState.value = HomeUiState.Error(e.message ?: "Terjadi kesalahan") }
                .collect { transactions ->
                    if (transactions.isEmpty()) {
                        _uiState.value = HomeUiState.Empty
                    } else {
                        val income = transactions.filter { it.type.name == "INCOME" }.sumOf { it.amount }
                        val expense = transactions.filter { it.type.name == "EXPENSE" }.sumOf { it.amount }
                        _uiState.value = HomeUiState.Success(
                            transactions = transactions,
                            balance = income - expense,
                            totalIncome = income,
                            totalExpense = expense
                        )
                    }
                }
        }
    }

    fun deleteTransaction(id: Int) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
        }
    }
}