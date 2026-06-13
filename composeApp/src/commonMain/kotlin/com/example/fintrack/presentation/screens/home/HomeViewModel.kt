package com.example.fintrack.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrack.data.local.datastore.UserPreferences

import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.repository.TransactionRepository
import com.example.fintrack.domain.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

import com.example.fintrack.domain.repository.AIRepository

class HomeViewModel(
    private val repository: TransactionRepository,
    private val aiRepository: AIRepository,
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

    private val _aiInsightText = MutableStateFlow("Menganalisis pola pengeluaranmu...")
    val aiInsightText: StateFlow<String> = _aiInsightText.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private var hasFetchedInsight = false

    init {
        viewModelScope.launch {
            combine(monthlyExpense, allTransactions) { expense, transactions ->
                Pair(expense, transactions)
            }.collect { (expense, transactions) ->
                if (expense > 0.0 && !hasFetchedInsight && transactions.isNotEmpty()) {
                    hasFetchedInsight = true
                    
                    val expenseTransactions = transactions.filter { it.type == TransactionType.EXPENSE }
                    val topCategoryName = if (expenseTransactions.isNotEmpty()) {
                        val categoryGroups = expenseTransactions.groupBy { it.category }
                        val topCat = categoryGroups.maxByOrNull { entry -> entry.value.sumOf { it.amount } }
                        topCat?.key ?: "Lainnya"
                    } else {
                        "Belum ada"
                    }
                    
                    fetchFinancialInsight(totalBalance.value, monthlyIncome.value, expense, topCategoryName, transactions)
                }
            }
        }
    }

    fun refreshInsight() {
        viewModelScope.launch {
            val balance = totalBalance.value
            val income = monthlyIncome.value
            val expense = monthlyExpense.value
            
            val transactions = allTransactions.first()
            val expenseTransactions = transactions.filter { it.type == TransactionType.EXPENSE }
            val topCategoryName = if (expenseTransactions.isNotEmpty()) {
                val categoryGroups = expenseTransactions.groupBy { it.category }
                val topCat = categoryGroups.maxByOrNull { entry -> entry.value.sumOf { it.amount } }
                topCat?.key ?: "Lainnya"
            } else {
                "Belum ada"
            }
            
            fetchFinancialInsight(balance, income, expense, topCategoryName, transactions)
        }
    }

    private fun fetchFinancialInsight(balance: Double, income: Double, expense: Double, topCategory: String, transactions: List<Transaction> = emptyList()) {
        viewModelScope.launch {
            _isAiLoading.value = true
            
            val txDetails = transactions.take(20).joinToString("\n") { 
                "- ${it.title} (${it.type.name}): ${com.example.fintrack.core.util.CurrencyFormatter.formatCurrency(it.amount, "USD")}"
            }
            
            val result = aiRepository.getFinancialInsight(
                totalBalance = balance,
                totalIncome = income,
                totalExpense = expense,
                budget = 500.0, // Bisa diganti dengan budget dari preferences kalau ada
                topCategory = topCategory,
                transactionDetails = txDetails
            )
            
            result.onSuccess { advice ->
                _aiInsightText.value = advice
            }.onFailure { error ->
                _aiInsightText.value = "Gagal memuat insight: ${error.message}"
            }
            _isAiLoading.value = false
        }
    }
}
