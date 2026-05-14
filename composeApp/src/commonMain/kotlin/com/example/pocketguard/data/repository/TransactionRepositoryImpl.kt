package com.example.pocketguard.data.repository

import com.example.pocketguard.data.local.PocketGuardDatabase
import com.example.pocketguard.domain.model.Transaction
import com.example.pocketguard.domain.model.TransactionCategory
import com.example.pocketguard.domain.model.TransactionType
import com.example.pocketguard.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map

class TransactionRepositoryImpl(database: PocketGuardDatabase) : TransactionRepository {
    private val queries = database.transactionQueries // Nama ini harus sesuai dengan file .sq Anda

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return queries.getAllTransactions()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities ->
                entities.map { entity ->
                    Transaction(
                        id = entity.id,
                        amount = entity.amount,
                        description = entity.description,
                        category = TransactionCategory.fromString(entity.category),
                        type = TransactionType.valueOf(entity.type),
                        createdAt = entity.created_at
                    )
                }
            }
    }

    override suspend fun insertTransaction(transaction: Transaction): Long {
        queries.insertTransaction(
            amount = transaction.amount,
            description = transaction.description,
            category = transaction.category.name,
            type = transaction.type.name,
            created_at = transaction.createdAt
        )
        return queries.lastInsertId().executeAsOne()
    }

    override suspend fun deleteTransaction(id: Long) {
        queries.deleteTransaction(id)
    }
}