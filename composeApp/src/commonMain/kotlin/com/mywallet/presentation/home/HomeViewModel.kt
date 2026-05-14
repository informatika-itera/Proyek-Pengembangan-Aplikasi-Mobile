package com.mywallet.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mywallet.domain.model.Transaction
import com.mywallet.domain.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            // Mock data using domain model
            val mockTransactions = listOf(
                Transaction(1, "Kiriman Orang Tua", 5000000.0, TransactionType.INCOME, "01 Oct 2023"),
                Transaction(2, "Transfer UKT", 3500000.0, TransactionType.EXPENSE, "02 Oct 2023"),
                Transaction(3, "Makan Siang", 50000.0, TransactionType.EXPENSE, "03 Oct 2023")
            )

            _uiState.value = HomeUiState(
                balance = "Rp 1.450.000",
                income = "Rp 5.000.000",
                expense = "Rp 3.550.000",
                transactions = mockTransactions,
                isLoading = false
            )
        }
    }
}
