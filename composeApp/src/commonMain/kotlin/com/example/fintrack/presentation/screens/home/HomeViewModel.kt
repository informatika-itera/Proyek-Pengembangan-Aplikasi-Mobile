package com.example.fintrack.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrack.data.local.datastore.UserPreferences
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.repository.TransactionRepository
import com.example.fintrack.domain.model.TransactionType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    repository: TransactionRepository,
    userPreferences: UserPreferences? = null
) : ViewModel() {

    val recentTransactions: StateFlow<List<Transaction>> = repository.getRecentTransactions(5)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val allTransactions = repository.getAllTransactions()

    val lastIdrRate: StateFlow<Double> = (userPreferences?.lastIdrRate ?: flowOf(16000.0))
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 16000.0
        )

    val totalBalance: StateFlow<Double> = allTransactions.map { transactions ->
        transactions.sumOf { tx ->
            if (tx.type == TransactionType.INCOME) tx.amount else -tx.amount
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    val monthlyIncome: StateFlow<Double> = allTransactions.map { transactions ->
        transactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    val monthlyExpense: StateFlow<Double> = allTransactions.map { transactions ->
        transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    val totalBalanceIdr: StateFlow<Double> = combine(
        totalBalance,
        lastIdrRate
    ) { balance, rate ->
        balance * rate
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    val monthlyIncomeIdr: StateFlow<Double> = combine(
        monthlyIncome,
        lastIdrRate
    ) { income, rate ->
        income * rate
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    val monthlyExpenseIdr: StateFlow<Double> = combine(
        monthlyExpense,
        lastIdrRate
    ) { expense, rate ->
        expense * rate
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )
}
