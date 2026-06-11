package com.example.travelplanner.domain.repository

import com.example.travelplanner.domain.model.Expense
import com.example.travelplanner.domain.model.TripFinanceSummary
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun getExpensesForTrip(tripId: String): Flow<List<Expense>>
    suspend fun saveExpense(expense: Expense)
    suspend fun deleteExpense(id: String)
    fun getTotalExpensesForTrip(tripId: String): Flow<Double>
    fun getFinanceSummaryList(): Flow<List<TripFinanceSummary>>
}
