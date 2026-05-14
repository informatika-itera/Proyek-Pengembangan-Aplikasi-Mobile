package com.example.pocketguard.domain.repository

import com.example.pocketguard.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
    suspend fun insertTransaction(transaction: Transaction): Long
    suspend fun deleteTransaction(id: Long)
}