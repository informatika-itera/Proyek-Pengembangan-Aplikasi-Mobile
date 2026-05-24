package com.example.mapenumkm.domain.repository

import com.example.mapenumkm.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
    suspend fun insertTransaction(transaction: Transaction): Long
    suspend fun deleteTransaction(id: Long)
}