package com.mywallet.presentation.home

import com.mywallet.domain.model.Transaction

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data object Empty : HomeUiState
    data class Success(
        val transactions: List<Transaction>,
        val balance: Double,
        val totalIncome: Double,
        val totalExpense: Double
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}