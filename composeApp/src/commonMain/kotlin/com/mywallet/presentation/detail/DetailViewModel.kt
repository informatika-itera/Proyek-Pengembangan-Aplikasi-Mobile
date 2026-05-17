package com.mywallet.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mywallet.domain.model.Transaction
import com.mywallet.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val transaction: Transaction) : DetailUiState
    data class Error(val message: String) : DetailUiState
}

class DetailViewModel(
    private val repository: TransactionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadTransaction(id: Int) {
        viewModelScope.launch {
            repository.getTransactionById(id)
                .collect { transaction ->
                    if (transaction != null) {
                        _uiState.value = DetailUiState.Success(transaction)
                    } else {
                        _uiState.value = DetailUiState.Error("Transaksi tidak ditemukan")
                    }
                }
        }
    }

    fun deleteTransaction(id: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
            onSuccess()
        }
    }
}