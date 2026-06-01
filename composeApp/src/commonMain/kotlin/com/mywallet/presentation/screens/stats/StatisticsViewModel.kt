package com.mywallet.presentation.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.ui.graphics.Color
import com.mywallet.domain.model.Transaction
import com.mywallet.domain.model.TransactionType
import com.mywallet.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

import com.mywallet.domain.repository.UserRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class StatisticsViewModel(
    private val repository: TransactionRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val categoryColors = mapOf(
        "Makanan" to Color(0xFFFF9800),
        "Transportasi" to Color(0xFF2196F3),
        "Belanja" to Color(0xFFE91E63),
        "Hiburan" to Color(0xFF9C27B0),
        "Kesehatan" to Color(0xFF4CAF50),
        "Tagihan" to Color(0xFFF44336),
        "Gaji" to Color(0xFF4CAF50),
        "Bonus" to Color(0xFFFFEB3B),
        "Investasi" to Color(0xFF00BCD4),
        "Hadiah" to Color(0xFFFFC107),
        "Tabungan" to Color(0xFF673AB7),
        "Lainnya" to Color(0xFF9E9E9E)
    )

    val uiState: StateFlow<StatisticsUiState> = repository.getAllTransactions().map { rawTransactions ->
        if (rawTransactions.isEmpty()) {
            StatisticsUiState.Success()
        } else {
            // Sort by ID to get recent transactions first (assuming ID is autoincrement)
            val transactions = rawTransactions.sortedBy { it.id }

            val income = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
            val expense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
            
            val savingsRate = if (income > 0) ((income - expense) / income) * 100 else 0.0
            
            val topExpenses = transactions
                .sortedByDescending { it.amount }
                .take(5)

            // Category Breakdown
            val categoryBreakdown = transactions
                .groupBy { it.category }
                .map { (category, list) ->
                    CategoryStat(
                        category = category,
                        amount = list.sumOf { it.amount },
                        color = categoryColors[category] ?: Color.Gray
                    )
                }
                .sortedByDescending { it.amount }

            // Group by date for weekly (last 7 days)
            val weeklyStats = transactions
                .groupBy { it.date }
                .map { (date, list) ->
                    val label = try {
                        if (date.contains("-")) {
                            val parts = date.split("-")
                            if (parts.size == 3) "${parts[2]}/${parts[1]}" else date
                        } else {
                            val parts = date.split(" ")
                            if (parts.size >= 2) "${parts[0]} ${parts[1].take(3)}" else date
                        }
                    } catch (e: Exception) {
                        date.take(5)
                    }
                    // Sum of all transactions (Net effect for the day or just activity volume?)
                    // Let's use total volume for the chart
                    DailyStat(label, list.sumOf { it.amount })
                }
                .takeLast(7)

            // Group by month for monthly
            val monthlyStats = transactions
                .groupBy { 
                    try { 
                        if (it.date.contains("-")) {
                            val monthNum = it.date.split("-")[1].toInt()
                            val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")
                            monthNames[monthNum - 1]
                        } else {
                            it.date.split(" ")[1] 
                        }
                    } catch(e: Exception) { "Unknown" }
                }
                .map { (month, list) -> DailyStat(month, list.sumOf { it.amount }) }

            // Group by year for yearly
            val yearlyStats = transactions
                .groupBy { 
                    try { 
                        if (it.date.contains("-")) it.date.split("-")[0]
                        else it.date.split(" ")[2] 
                    } catch(e: Exception) { "Year" }
                }
                .map { (year, list) -> DailyStat(year, list.sumOf { it.amount }) }

            StatisticsUiState.Success(
                totalIncome = income,
                totalExpense = expense,
                savingsRate = savingsRate,
                topExpenses = topExpenses,
                weeklyData = weeklyStats,
                monthlyData = monthlyStats,
                yearlyData = yearlyStats,
                categoryBreakdown = categoryBreakdown
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatisticsUiState.Loading
    )
}
