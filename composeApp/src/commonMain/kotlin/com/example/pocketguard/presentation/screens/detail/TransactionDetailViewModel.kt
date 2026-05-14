package com.example.pocketguard.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketguard.domain.model.Transaction
import com.example.pocketguard.domain.model.TransactionType
import com.example.pocketguard.domain.repository.TransactionRepository
import com.example.pocketguard.domain.usecase.DeleteTransactionUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransactionDetailViewModel(
    private val repository: TransactionRepository,
    private val deleteTransactionUseCase: DeleteTransactionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TransactionDetailUiState>(TransactionDetailUiState.Loading)
    val uiState: StateFlow<TransactionDetailUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<TransactionDetailEvent>()
    val events: SharedFlow<TransactionDetailEvent> = _events.asSharedFlow()

    fun loadTransaction(transactionId: Long) {
        viewModelScope.launch {
            // Mengambil semua transaksi dan mencari berdasarkan ID
            repository.getAllTransactions().collect { transactions ->
                val transaction = transactions.find { it.id == transactionId }
                _uiState.value = if (transaction != null) {
                    TransactionDetailUiState.Success(transaction)
                } else {
                    TransactionDetailUiState.NotFound
                }
            }
        }
    }

    fun deleteTransaction() {
        val currentState = _uiState.value
        if (currentState is TransactionDetailUiState.Success) {
            viewModelScope.launch {
                deleteTransactionUseCase(currentState.transaction.id)
                    .onSuccess {
                        _events.emit(TransactionDetailEvent.TransactionDeleted)
                    }
                    .onFailure { error ->
                        _events.emit(TransactionDetailEvent.Error(error.message ?: "Gagal menghapus transaksi"))
                    }
            }
        }
    }

    fun getShareContent(): String? {
        val currentState = _uiState.value
        return if (currentState is TransactionDetailUiState.Success) {
            val transaction = currentState.transaction
            val typePrefix = if (transaction.type == TransactionType.INCOME) "[Pemasukan]" else "[Pengeluaran]"

            buildString {
                appendLine("Detail Transaksi PocketGuard")
                appendLine("---------------------------")
                appendLine("Tipe: $typePrefix")
                appendLine("Nominal: Rp ${transaction.amount}")
                appendLine("Kategori: ${transaction.category.displayName}")
                if (transaction.description.isNotBlank()) {
                    appendLine("Deskripsi: ${transaction.description}")
                }
            }
        } else null
    }
}

sealed interface TransactionDetailUiState {
    data object Loading : TransactionDetailUiState
    data class Success(val transaction: Transaction) : TransactionDetailUiState
    data object NotFound : TransactionDetailUiState
}

sealed interface TransactionDetailEvent {
    data object TransactionDeleted : TransactionDetailEvent
    data class Error(val message: String) : TransactionDetailEvent
}