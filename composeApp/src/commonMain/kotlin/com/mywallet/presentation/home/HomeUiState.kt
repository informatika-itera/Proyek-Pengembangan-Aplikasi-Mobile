package com.mywallet.presentation.home

import com.mywallet.domain.model.Transaction

data class HomeUiState(
    val balance: String = "Rp 0",
    val income: String = "Rp 0",
    val expense: String = "Rp 0",
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = false
)
