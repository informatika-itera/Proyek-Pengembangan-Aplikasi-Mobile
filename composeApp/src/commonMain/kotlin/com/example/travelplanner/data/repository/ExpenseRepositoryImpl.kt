package com.example.travelplanner.data.repository

import com.example.travelplanner.domain.model.Expense
import com.example.travelplanner.domain.repository.ExpenseRepository
import com.example.travelplanner.data.local.TravelPlannerDatabase
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ExpenseRepositoryImpl(
    private val database: TravelPlannerDatabase
) : ExpenseRepository {

    private val queries = database.travelDatabaseQueries

    override fun getExpensesForTrip(tripId: String): Flow<List<Expense>> {
        return queries.getExpensesForTrip(tripId)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities ->
                entities.map { entity ->
                    Expense(
                        id = entity.id,
                        tripId = entity.trip_id,
                        namaItem = entity.nama_item,
                        nominal = entity.nominal,
                        kategori = entity.kategori,
                        createdAt = entity.created_at
                    )
                }
            }
    }

    override suspend fun saveExpense(expense: Expense) {
        queries.insertExpense(
            id = expense.id,
            trip_id = expense.tripId,
            nama_item = expense.namaItem,
            nominal = expense.nominal,
            kategori = expense.kategori,
            created_at = expense.createdAt
        )
    }

    override suspend fun deleteExpense(id: String) {
        queries.deleteExpense(id)
    }

    override fun getTotalExpensesForTrip(tripId: String): Flow<Double> {
        return queries.getTotalExpensesForTrip(tripId)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { sum -> sum?.SUM ?: 0.0 }
    }
}
