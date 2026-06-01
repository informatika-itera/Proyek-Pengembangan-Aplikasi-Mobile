package com.mywallet.presentation.screens.home

import com.mywallet.domain.model.Transaction

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data object Empty : HomeUiState
    data class Success(
        val transactions: List<Transaction>,
        val totalIncome: Double,
        val totalExpense: Double,
        val balance: Double
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}