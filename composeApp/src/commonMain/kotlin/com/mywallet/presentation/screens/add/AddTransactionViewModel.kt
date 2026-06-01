package com.mywallet.presentation.screens.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mywallet.domain.model.Transaction
import com.mywallet.domain.model.TransactionType
import com.mywallet.domain.repository.TransactionRepository
import com.mywallet.utils.getCurrentIsoDate
import com.mywallet.utils.getCurrentTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AddTransactionUiState(
    val title: String = "",
    val amount: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val category: String = "Lainnya", 
    val date: String = getCurrentIsoDate(), // Default ke hari ini
    val time: String = getCurrentTime(), // Default ke waktu sekarang
    val isRecurring: Boolean = false,
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

    fun onCategoryChange(value: String) {
        _uiState.value = _uiState.value.copy(category = value)
    }

    fun onDateChange(value: String) {
        _uiState.value = _uiState.value.copy(date = value)
    }

    fun onTimeChange(value: String) {
        _uiState.value = _uiState.value.copy(time = value)
    }

    fun onRecurringChange(value: Boolean) {
        _uiState.value = _uiState.value.copy(isRecurring = value)
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
        
        _uiState.value = state.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                repository.insertTransaction(
                    Transaction(
                        id = 0,
                        title = state.title,
                        amount = amount,
                        type = state.type,
                        category = state.category,
                        date = state.date,
                        time = if (state.time.isBlank()) "00:00" else state.time,
                        isRecurring = state.isRecurring
                    )
                )
                _uiState.value = _uiState.value.copy(isLoading = false, isSaved = true)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Gagal menyimpan transaksi: ${e.message}")
            }
        }
    }

    fun updateTransaction(id: Int, onSuccess: () -> Unit) {
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
        
        _uiState.value = state.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                repository.updateTransaction(
                    Transaction(
                        id = id,
                        title = state.title,
                        amount = amount,
                        type = state.type,
                        category = state.category,
                        date = state.date,
                        time = if (state.time.isBlank()) "00:00" else state.time,
                        isRecurring = state.isRecurring
                    )
                )
                _uiState.value = _uiState.value.copy(isLoading = false, isSaved = true)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Gagal memperbarui transaksi: ${e.message}")
            }
        }
    }
}