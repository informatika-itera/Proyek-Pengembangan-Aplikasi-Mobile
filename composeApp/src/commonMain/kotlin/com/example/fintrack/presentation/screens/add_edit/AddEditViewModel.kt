package com.example.fintrack.presentation.screens.add_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrack.data.local.datastore.UserPreferences
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.model.TransactionType
import com.example.fintrack.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.math.roundToLong

class AddEditViewModel(
    private val repository: TransactionRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditState())
    val uiState = _uiState.asStateFlow()

    /**
     * Observe the cached IDR rate as a StateFlow so saveTransaction can read
     * its latest value without suspending inside a non-suspend lambda.
     */
    private val idrRate: kotlinx.coroutines.flow.StateFlow<Double> = userPreferences.lastIdrRate
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 16000.0
        )

    fun loadTransaction(id: Long) {
        viewModelScope.launch {
            repository.getTransactionById(id).collect { transaction ->
                if (transaction != null) {
                    val currentIdrRate = idrRate.value
                    val displayAmount = if (transaction.currency == "IDR") {
                        (transaction.amount * currentIdrRate).roundToLong().toString()
                    } else {
                        transaction.amount.toString()
                    }
                    _uiState.update {
                        it.copy(
                            id = transaction.id,
                            title = transaction.title,
                            amount = displayAmount,
                            type = transaction.type,
                            category = transaction.category,
                            currency = transaction.currency,
                            dateMillis = transaction.date.toEpochMilliseconds()
                        )
                    }
                }
            }
        }
    }

    fun onEvent(event: AddEditEvent) {
        when (event) {
            is AddEditEvent.TitleChanged -> _uiState.update { it.copy(title = event.title) }
            is AddEditEvent.AmountChanged -> _uiState.update { it.copy(amount = event.amount) }
            is AddEditEvent.TypeChanged -> _uiState.update { it.copy(type = event.type) }
            is AddEditEvent.CategoryChanged -> _uiState.update { it.copy(category = event.category) }
            is AddEditEvent.CurrencyChanged -> _uiState.update { it.copy(currency = event.currency) }
            is AddEditEvent.DateChanged -> _uiState.update { it.copy(dateMillis = event.millis) }
            is AddEditEvent.SaveTransaction -> saveTransaction(event.onSuccess)
        }
    }

    private fun saveTransaction(onSuccess: () -> Unit) {
        val state = _uiState.value
        val amountDouble = state.amount.toDoubleOrNull() ?: 0.0
        if (state.title.isBlank() || amountDouble <= 0.0) return

        val resolvedDate = state.dateMillis
            ?.let { Instant.fromEpochMilliseconds(it) }
            ?: Clock.System.now()

        // If the user entered an IDR amount, convert it to USD before persisting
        // so the database always stores values in USD.
        val currentIdrRate = idrRate.value
        val resolvedAmount = if (state.currency == "IDR") {
            amountDouble / currentIdrRate
        } else {
            amountDouble
        }

        val transaction = Transaction(
            id = state.id ?: 0,
            title = state.title,
            amount = resolvedAmount,
            type = state.type,
            category = state.category.ifBlank { "General" },
            date = resolvedDate,
            currency = state.currency
        )

        viewModelScope.launch {
            if (state.id == null || state.id == 0L) {
                repository.insertTransaction(transaction)
            } else {
                repository.updateTransaction(transaction)
            }
            onSuccess()
        }
    }
}

data class AddEditState(
    val id: Long? = null,
    val title: String = "",
    val amount: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val category: String = "",
    val currency: String = "USD",
    val dateMillis: Long? = null
)

sealed class AddEditEvent {
    data class TitleChanged(val title: String) : AddEditEvent()
    data class AmountChanged(val amount: String) : AddEditEvent()
    data class TypeChanged(val type: TransactionType) : AddEditEvent()
    data class CategoryChanged(val category: String) : AddEditEvent()
    data class CurrencyChanged(val currency: String) : AddEditEvent()
    data class DateChanged(val millis: Long) : AddEditEvent()
    data class SaveTransaction(val onSuccess: () -> Unit) : AddEditEvent()
}
