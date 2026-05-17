package com.mywallet.data.repository

import com.mywallet.data.local.TransactionLocalDataSource
import com.mywallet.data.model.toDomain
import com.mywallet.data.model.toEntity
import com.mywallet.domain.model.Transaction
import com.mywallet.domain.model.TransactionType
import com.mywallet.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepositoryImpl(
    private val localDataSource: TransactionLocalDataSource
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> =
        localDataSource.getAllTransactions().map { list -> list.map { it.toDomain() } }

    override fun getTransactionById(id: Int): Flow<Transaction?> =
        localDataSource.getTransactionById(id).map { it?.toDomain() }

    override fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> =
        localDataSource.getTransactionsByType(type.name).map { list -> list.map { it.toDomain() } }

    override suspend fun insertTransaction(transaction: Transaction) =
        localDataSource.insertTransaction(transaction.toEntity())

    override suspend fun updateTransaction(transaction: Transaction) =
        localDataSource.updateTransaction(transaction.toEntity())

    override suspend fun deleteTransaction(id: Int) =
        localDataSource.deleteTransaction(id)
}