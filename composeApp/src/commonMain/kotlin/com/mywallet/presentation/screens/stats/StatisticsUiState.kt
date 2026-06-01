package com.mywallet.presentation.screens.stats

import androidx.compose.ui.graphics.Color
import com.mywallet.domain.model.Transaction

data class CategoryStat(val category: String, val amount: Double, val color: Color)

sealed interface StatisticsUiState {
    object Loading : StatisticsUiState
    data class Success(
        val totalIncome: Double = 0.0,
        val totalExpense: Double = 0.0,
        val savingsRate: Double = 0.0,
        val topExpenses: List<Transaction> = emptyList(),
        val weeklyData: List<DailyStat> = emptyList(),
        val monthlyData: List<DailyStat> = emptyList(),
        val yearlyData: List<DailyStat> = emptyList(),
        val categoryBreakdown: List<CategoryStat> = emptyList() // Added category breakdown
    ) : StatisticsUiState
}

data class DailyStat(
    val day: String,
    val amount: Double
)
