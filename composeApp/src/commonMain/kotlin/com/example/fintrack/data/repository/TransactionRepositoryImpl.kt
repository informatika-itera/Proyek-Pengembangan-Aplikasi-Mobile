package com.example.fintrack.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.fintrack.data.local.FinTrackDatabase
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.model.TransactionType
import com.example.fintrack.domain.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant

class TransactionRepositoryImpl(
    private val db: FinTrackDatabase
) : TransactionRepository {
    
    private val queries = db.transactionQueries
    
    override fun getAllTransactions(): Flow<List<Transaction>> {
        return queries.getAllTransactions()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomainModel() } }
            .flowOn(Dispatchers.IO)
    }
    
    override fun getRecentTransactions(limit: Long): Flow<List<Transaction>> {
        return queries.getRecentTransactions(limit)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomainModel() } }
            .flowOn(Dispatchers.IO)
    }
    
    override fun getTransactionById(id: Long): Flow<Transaction?> {
        return queries.getTransactionById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toDomainModel() }
            .flowOn(Dispatchers.IO)
    }
    
    override fun getTotalBalance(): Flow<Double> {
        return queries.getTotalBalance()
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.SUM ?: 0.0 }
            .flowOn(Dispatchers.IO)
    }
    
    override suspend fun insertTransaction(transaction: Transaction): Long = withContext(Dispatchers.IO) {
        queries.insertTransaction(
            title = transaction.title,
            amount = transaction.amount,
            type = transaction.type.name,
            category = transaction.category,
            date = transaction.date.toEpochMilliseconds(),
            created_at = transaction.createdAt.toEpochMilliseconds(),
            updated_at = transaction.updatedAt.toEpochMilliseconds(),
            currency = transaction.currency
        )
        queries.lastInsertId().executeAsOne()
    }
    
    override suspend fun updateTransaction(transaction: Transaction): Unit = withContext(Dispatchers.IO) {
        queries.updateTransaction(
            id = transaction.id,
            title = transaction.title,
            amount = transaction.amount,
            type = transaction.type.name,
            category = transaction.category,
            date = transaction.date.toEpochMilliseconds(),
            updated_at = transaction.updatedAt.toEpochMilliseconds(),
            currency = transaction.currency
        )
    }
    
    override suspend fun deleteTransaction(id: Long): Unit = withContext(Dispatchers.IO) {
        queries.deleteTransactionById(id)
    }
    
    private fun com.example.fintrack.data.local.TransactionEntity.toDomainModel(): Transaction {
        return Transaction(
            id = id,
            title = title,
            amount = amount,
            type = TransactionType.fromString(type),
            category = category,
            date = Instant.fromEpochMilliseconds(date),
            createdAt = Instant.fromEpochMilliseconds(created_at),
            updatedAt = Instant.fromEpochMilliseconds(updated_at),
            currency = currency
        )
    }
}
