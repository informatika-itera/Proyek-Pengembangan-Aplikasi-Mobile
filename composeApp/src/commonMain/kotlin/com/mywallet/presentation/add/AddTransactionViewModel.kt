package com.mywallet.presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mywallet.domain.model.Transaction
import com.mywallet.domain.model.TransactionType
import com.mywallet.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AddTransactionUiState(
    val title: String = "",
    val amount: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val date: String = "",
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)

class AddTransactionViewModel(
    private val repository: TransactionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    fun onTitleChange(value: String) {
        _uiState.value = _uiState.value.copy(title = value)
    }

    fun onAmountChange(value: String) {
        _uiState.value = _uiState.value.copy(amount = value)
    }

    fun onTypeChange(value: TransactionType) {
        _uiState.value = _uiState.value.copy(type = value)
    }

    fun onDateChange(value: String) {
        _uiState.value = _uiState.value.copy(date = value)
    }

    fun saveTransaction(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.title.isBlank() || state.amount.isBlank() || state.date.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Semua field harus diisi")
            return
        }
        val amount = state.amount.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _uiState.value = state.copy(errorMessage = "Nominal tidak valid")
            return
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = null)
            repository.insertTransaction(
                Transaction(
                    id = 0,
                    title = state.title,
                    amount = amount,
                    type = state.type,
                    date = state.date
                )
            )
            _uiState.value = _uiState.value.copy(isLoading = false, isSaved = true)
            onSuccess()
        }
    }
}