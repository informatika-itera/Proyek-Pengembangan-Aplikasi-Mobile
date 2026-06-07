package com.example.pocketguard.presentation.screens.add_transaction


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketguard.domain.model.Transaction
import com.example.pocketguard.domain.model.TransactionCategory
import com.example.pocketguard.domain.model.TransactionType
import com.example.pocketguard.domain.repository.TransactionRepository
import com.example.pocketguard.domain.usecase.SaveTransactionUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock


class AddTransactionViewModel(
    private val repository: TransactionRepository,
    private val saveTransactionUseCase: SaveTransactionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AddTransactionEvent>()
    val events: SharedFlow<AddTransactionEvent> = _events.asSharedFlow()

    private var currentTransactionId: Long? = null

    fun loadTransaction(transactionId: Long) {
        currentTransactionId = transactionId
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            // Mengambil data dari repository untuk mode Edit
            repository.getAllTransactions().collect { transactions ->
                val transaction = transactions.find { it.id == transactionId }
                transaction?.let {
                    _uiState.update { state ->
                        state.copy(
                            amount = transaction.amount.toString(),
                            description = transaction.description,
                            category = transaction.category,
                            type = transaction.type,
                            isLoading = false,
                            isEditMode = true,
                            createdAt = transaction.createdAt
                        )
                    }
                }
            }
        }
    }

    // ==================== USER ACTIONS ====================

    fun onAmountChange(newAmount: String) {
        // Hanya izinkan input angka
        val cleanValue = newAmount.replace(".","")
        if (cleanValue.all { it.isDigit() }) {
            _uiState.update { it.copy(amount = cleanValue, amountError = null)}
        }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun onCategoryChange(category: TransactionCategory) {
        _uiState.update { it.copy(category = category) }
    }
    fun onDateChange(timestamp: Long) {
        _uiState.update { it.copy(createdAt = timestamp) }
    }

    fun onTypeChange(type: TransactionType) {
        _uiState.update { state ->
            // Logika pemisahan kategori
            val newCategory = if (type == TransactionType.INCOME) {
                // Jika Pemasukan, paksa kategori menjadi SALARY (Gaji)
                TransactionCategory.SALARY
            } else if (state.category == TransactionCategory.SALARY) {
                // Jika pindah ke Pengeluaran tapi kategori masih Gaji, ubah ke OTHER/FOOD agar tidak error
                TransactionCategory.OTHER
            } else {
                state.category
            }

            state.copy(type = type, category = newCategory)
        }
    }

    fun saveTransaction() {
        val state = _uiState.value
        val amountDouble = state.amount.toDoubleOrNull() ?: 0.0

        if (amountDouble <= 0) {
            _uiState.update { it.copy(amountError = "Nominal harus lebih dari 0") }
            return
        }

        if (state.description.isBlank()) {
            _uiState.update { it.copy(description = "Deskripsi tidak boleh kosong") }
            // Anda bisa menambah descriptionError di UI State jika perlu
            return
        }

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            val transaction = Transaction(
                id = currentTransactionId ?: 0,
                amount = amountDouble,
                description = state.description.trim(),
                category = state.category,
                type = state.type,
                createdAt = state.createdAt
            )

            saveTransactionUseCase(transaction)
                .onSuccess {
                    _events.emit(AddTransactionEvent.TransactionSaved)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isSaving = false) }
                    _events.emit(AddTransactionEvent.Error(error.message ?: "Gagal menyimpan"))
                }
        }
    }
}

data class AddTransactionUiState(
    val amount: String = "",
    val description: String = "",
    val category: TransactionCategory = TransactionCategory.OTHER,
    val type: TransactionType = TransactionType.EXPENSE,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isEditMode: Boolean = false,
    val amountError: String? = null,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds()
) {
    val canSave: Boolean
        get() = amount.isNotBlank() && !isSaving
}

sealed interface AddTransactionEvent {
    data object TransactionSaved : AddTransactionEvent
    data class Error(val message: String) : AddTransactionEvent
}