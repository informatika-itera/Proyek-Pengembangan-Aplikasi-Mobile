package com.mywallet.domain.repository

import com.mywallet.domain.model.Transaction
import com.mywallet.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
    fun getTransactionById(id: Int): Flow<Transaction?>
    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>>
    suspend fun insertTransaction(transaction: Transaction)
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun deleteTransaction(id: Int)
}