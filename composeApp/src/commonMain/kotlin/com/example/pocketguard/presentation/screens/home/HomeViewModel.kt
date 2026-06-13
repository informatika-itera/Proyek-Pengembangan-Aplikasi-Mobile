package com.example.pocketguard.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketguard.data.local.datastore.UserPreferences
import com.example.pocketguard.domain.model.Transaction
import com.example.pocketguard.domain.model.TransactionType
import com.example.pocketguard.domain.usecase.DeleteTransactionUseCase
import com.example.pocketguard.domain.usecase.GetAllTransactionsUseCase
import com.example.pocketguard.domain.usecase.TransactionSortBy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class HomeViewModel(
    private val getAllTransactionsUseCase: GetAllTransactionsUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _query = MutableStateFlow("")
    private val _selectedMonth = MutableStateFlow<String?>(null)
    private val _sortBy = MutableStateFlow(TransactionSortBy.DATE_DESC)
    val sortBy: StateFlow<TransactionSortBy> = _sortBy.asStateFlow()

    private fun getCurrentMonthYear(): String {
        val now = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        return getMonthYear(now)
    }

    // Mengambil batas anggaran sesuai bulan yang diklik (atau bulan ini jika "Semua Waktu" dipilih)
    @OptIn(ExperimentalCoroutinesApi::class)
    private val dynamicBudgetFlow = _selectedMonth.flatMapLatest { month ->
        val targetMonth = month ?: getCurrentMonthYear()
        userPreferences.getBudgetLimit(targetMonth).map { limit -> Pair(targetMonth, limit) }
    }

    val uiState: StateFlow<HomeUiState> = combine(
        _query,
        _selectedMonth,
        _sortBy,
        getAllTransactionsUseCase(),
        dynamicBudgetFlow
    ) { query, selectedMonth, sort, transactions, budgetData ->

        val activeBudgetMonth = budgetData.first
        val budgetLimit = budgetData.second

        val allTimeIncome = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val allTimeExpense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val absoluteTotalBalance = allTimeIncome - allTimeExpense

        // Menghitung pengeluaran KHUSUS untuk bulan target (agar progress bar akurat)
        val budgetExpense = transactions
            .filter { it.type == TransactionType.EXPENSE && getMonthYear(it.createdAt) == activeBudgetMonth }
            .sumOf { it.amount }

        val availableMonths = transactions.map { getMonthYear(it.createdAt) }.distinct()

        val filteredByQuery = if (query.isBlank()) transactions else transactions.filter { it.description.contains(query, ignoreCase = true) }
        val filteredByMonth = if (selectedMonth == null) filteredByQuery else filteredByQuery.filter { getMonthYear(it.createdAt) == selectedMonth }

        val sortedTransactions = when (sort) {
            TransactionSortBy.DATE_ASC -> filteredByMonth.sortedBy { it.createdAt }
            TransactionSortBy.DATE_DESC -> filteredByMonth.sortedByDescending { it.createdAt }
            TransactionSortBy.AMOUNT_ASC -> filteredByMonth.sortedBy { it.amount }
            TransactionSortBy.AMOUNT_DESC -> filteredByMonth.sortedByDescending { it.amount }
            TransactionSortBy.CATEGORY -> filteredByMonth.sortedBy { it.category.name }
        }

        if (sortedTransactions.isEmpty()) {
            HomeUiState.Empty(query, selectedMonth, availableMonths, absoluteTotalBalance, budgetLimit, budgetExpense, activeBudgetMonth)
        } else {
            HomeUiState.Success(sortedTransactions, query, selectedMonth, availableMonths, absoluteTotalBalance, budgetLimit, budgetExpense, activeBudgetMonth)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState.Loading
    )

    fun onSearchQueryChange(newQuery: String) { _query.value = newQuery }
    fun clearSearch() { _query.value = ""; _selectedMonth.value = null }
    fun onMonthSelected(month: String?) { _selectedMonth.value = month }
    fun onSortByChanged(sort: TransactionSortBy) { _sortBy.value = sort }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch { deleteTransactionUseCase(id) }
    }

    fun updateBudgetLimit(limit: Double) {
        viewModelScope.launch {
            val targetMonth = _selectedMonth.value ?: getCurrentMonthYear()
            userPreferences.setBudgetLimit(targetMonth, limit)
        }
    }

    private fun getMonthYear(timestamp: Long): String {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val monthNames = arrayOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des")
        return "${monthNames[dateTime.monthNumber - 1]} ${dateTime.year}"
    }
}

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val transactions: List<Transaction>,
        val query: String,
        val selectedMonth: String?,
        val availableMonths: List<String>,
        val totalBalance: Double,
        val budgetLimit: Double,
        val budgetExpense: Double,      // BARU
        val activeBudgetMonth: String   // BARU
    ) : HomeUiState
    data class Empty(
        val query: String,
        val selectedMonth: String?,
        val availableMonths: List<String>,
        val totalBalance: Double,
        val budgetLimit: Double,
        val budgetExpense: Double,      // BARU
        val activeBudgetMonth: String   // BARU
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}