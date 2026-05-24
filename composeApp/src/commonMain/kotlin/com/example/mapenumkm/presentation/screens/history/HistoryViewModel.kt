package com.example.mapenumkm.presentation.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapenumkm.domain.model.Transaction
import com.example.mapenumkm.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

sealed interface HistoryUiState {
    data object Loading : HistoryUiState
    data class Success(
        val transactions: List<Transaction>,
        val selectedFilter: HistoryFilter = HistoryFilter.ALL
    ) : HistoryUiState
    data class Error(val message: String) : HistoryUiState
}

enum class HistoryFilter {
    ALL, TODAY, THIS_WEEK, THIS_MONTH
}

class HistoryViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _filter = MutableStateFlow(HistoryFilter.ALL)

    val uiState: StateFlow<HistoryUiState> = combine(
        transactionRepository.getAllTransactions(),
        _filter
    ) { transactions, filter ->
        val now = Clock.System.now()
        val systemTZ = TimeZone.currentSystemDefault()
        val today = now.toLocalDateTime(systemTZ).date

        val filteredTransactions = when (filter) {
            HistoryFilter.ALL -> transactions
            HistoryFilter.TODAY -> transactions.filter {
                it.createdAt.toLocalDateTime(systemTZ).date == today
            }
            HistoryFilter.THIS_WEEK -> {
                val startOfWeek = now.minus(7, DateTimeUnit.DAY, systemTZ)
                transactions.filter { it.createdAt >= startOfWeek }
            }
            HistoryFilter.THIS_MONTH -> {
                val startOfMonth = now.toLocalDateTime(systemTZ).let {
                    it.date.minus(it.dayOfMonth - 1, DateTimeUnit.DAY)
                }
                transactions.filter { 
                    it.createdAt.toLocalDateTime(systemTZ).date >= startOfMonth 
                }
            }
        }

        HistoryUiState.Success(
            transactions = filteredTransactions.sortedByDescending { it.createdAt },
            selectedFilter = filter
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HistoryUiState.Loading
    )

    fun onFilterSelected(filter: HistoryFilter) {
        _filter.value = filter
    }
}
