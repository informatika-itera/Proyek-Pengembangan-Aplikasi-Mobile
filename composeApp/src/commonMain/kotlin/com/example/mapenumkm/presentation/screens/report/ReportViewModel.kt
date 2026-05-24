package com.example.mapenumkm.presentation.screens.report

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
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.atTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

data class ChartData(
    val value: Float,
    val label: String
)

data class TopProduct(
    val name: String,
    val quantity: Int,
    val totalSales: Double,
    val imageUrl: String? = null
)

data class ReportUiState(
    val totalSales: Double = 0.0,
    val totalTransactions: Int = 0,
    val totalProductsSold: Int = 0,
    val averageTransactionValue: Double = 0.0,
    val transactions: List<Transaction> = emptyList(),
    val graphData: List<ChartData> = emptyList(),
    val topProducts: List<TopProduct> = emptyList(),
    val selectedFilter: ReportFilter = ReportFilter.DAILY,
    val selectedDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
    val isLoading: Boolean = true
)

enum class ReportFilter {
    DAILY, WEEKLY, MONTHLY
}

class ReportViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _filter = MutableStateFlow(ReportFilter.DAILY)
    private val _selectedDate = MutableStateFlow(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)

    val uiState: StateFlow<ReportUiState> = combine(
        transactionRepository.getAllTransactions(),
        _filter,
        _selectedDate
    ) { transactions, filter, selectedDate ->
        val systemTZ = TimeZone.currentSystemDefault()

        val filteredTransactions = when (filter) {
            ReportFilter.DAILY -> transactions.filter {
                it.createdAt.toLocalDateTime(systemTZ).date == selectedDate
            }
            ReportFilter.WEEKLY -> {
                // Last 7 days including selectedDate
                val startOfPeriod = selectedDate.minus(6, DateTimeUnit.DAY)
                val startInstant = startOfPeriod.atStartOfDayIn(systemTZ)
                val endInstant = selectedDate.atTime(23, 59, 59).toInstant(systemTZ)
                transactions.filter { it.createdAt in startInstant..endInstant }
            }
            ReportFilter.MONTHLY -> {
                // Full month of selectedDate
                val startOfMonth = LocalDate(selectedDate.year, selectedDate.month, 1)
                val nextMonth = if (selectedDate.monthNumber == 12) {
                    LocalDate(selectedDate.year + 1, 1, 1)
                } else {
                    LocalDate(selectedDate.year, selectedDate.monthNumber + 1, 1)
                }
                val startInstant = startOfMonth.atStartOfDayIn(systemTZ)
                val endInstant = nextMonth.atStartOfDayIn(systemTZ).minus(1, DateTimeUnit.SECOND)
                
                transactions.filter {
                    it.createdAt in startInstant..endInstant
                }
            }
        }

        val totalSales = filteredTransactions.sumOf { it.total }
        val totalProductsSold = filteredTransactions.sumOf { t -> t.items.sumOf { it.quantity } }

        val graphData = calculateGraphData(filteredTransactions, filter, selectedDate, systemTZ)
        val topProducts = calculateTopProducts(filteredTransactions)

        ReportUiState(
            totalSales = totalSales,
            totalTransactions = filteredTransactions.size,
            totalProductsSold = totalProductsSold,
            averageTransactionValue = if (filteredTransactions.isNotEmpty()) totalSales / filteredTransactions.size else 0.0,
            transactions = filteredTransactions,
            graphData = graphData,
            topProducts = topProducts,
            selectedFilter = filter,
            selectedDate = selectedDate,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ReportUiState()
    )

    private fun calculateGraphData(
        transactions: List<Transaction>,
        filter: ReportFilter,
        selectedDate: LocalDate,
        timeZone: TimeZone
    ): List<ChartData> {
        return when (filter) {
            ReportFilter.DAILY -> {
                // Hourly for selected date (0-23)
                (0..23 step 2).map { hour ->
                    val total = transactions.filter {
                        val tHour = it.createdAt.toLocalDateTime(timeZone).hour
                        tHour == hour || tHour == hour + 1
                    }.sumOf { it.total }.toFloat()
                    ChartData(total, "${hour.toString().padStart(2, '0')}:00")
                }
            }
            ReportFilter.WEEKLY -> {
                // Last 7 days including selected date
                (6 downTo 0).map { i ->
                    val date = selectedDate.minus(i, DateTimeUnit.DAY)
                    val total = transactions.filter {
                        it.createdAt.toLocalDateTime(timeZone).date == date
                    }.sumOf { it.total }.toFloat()
                    ChartData(total, "${date.dayOfMonth}/${date.monthNumber}")
                }
            }
            ReportFilter.MONTHLY -> {
                // Full month of selectedDate
                val startOfMonth = LocalDate(selectedDate.year, selectedDate.month, 1)
                val nextMonth = if (selectedDate.monthNumber == 12) {
                    LocalDate(selectedDate.year + 1, 1, 1)
                } else {
                    LocalDate(selectedDate.year, selectedDate.monthNumber + 1, 1)
                }
                val lastDay = nextMonth.minus(1, DateTimeUnit.DAY).dayOfMonth
                
                val step = (lastDay / 10).coerceAtLeast(1)
                
                (0 until lastDay step step).map { i ->
                    val date = LocalDate(selectedDate.year, selectedDate.month, i + 1)
                    val total = transactions.filter {
                        it.createdAt.toLocalDateTime(timeZone).date == date
                    }.sumOf { it.total }.toFloat()
                    ChartData(total, "${date.dayOfMonth}/${date.monthNumber}")
                }
            }
        }
    }

    private fun calculateTopProducts(transactions: List<Transaction>): List<TopProduct> {
        return transactions.flatMap { it.items }
            .groupBy { it.productId }
            .map { (productId, items) ->
                TopProduct(
                    name = items.first().productName,
                    quantity = items.sumOf { it.quantity },
                    totalSales = items.sumOf { it.totalPrice },
                    imageUrl = items.first().imageUrl
                )
            }
            .sortedByDescending { it.quantity }
    }

    fun onFilterSelected(filter: ReportFilter) {
        _filter.value = filter
    }

    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
    }

    fun onPreviousDate() {
        val current = _selectedDate.value
        _selectedDate.value = when (_filter.value) {
            ReportFilter.DAILY -> current.minus(1, DateTimeUnit.DAY)
            ReportFilter.WEEKLY -> current.minus(7, DateTimeUnit.DAY)
            ReportFilter.MONTHLY -> {
                if (current.monthNumber == 1) LocalDate(current.year - 1, 12, 1)
                else LocalDate(current.year, current.monthNumber - 1, 1)
            }
        }
    }

    fun onNextDate() {
        val current = _selectedDate.value
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        
        val next = when (_filter.value) {
            ReportFilter.DAILY -> current.plus(1, DateTimeUnit.DAY)
            ReportFilter.WEEKLY -> current.plus(7, DateTimeUnit.DAY)
            ReportFilter.MONTHLY -> {
                if (current.monthNumber == 12) LocalDate(current.year + 1, 1, 1)
                else LocalDate(current.year, current.monthNumber + 1, 1)
            }
        }
        
        if (next <= today) {
            _selectedDate.value = next
        }
    }
}
