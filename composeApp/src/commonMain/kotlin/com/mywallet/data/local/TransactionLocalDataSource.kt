package com.mywallet.data.local

import com.mywallet.data.model.TransactionEntity
import com.mywallet.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class TransactionLocalDataSource {
    private val transactions = MutableStateFlow<List<TransactionEntity>>(
        listOf(
            TransactionEntity(1, "Kiriman Orang Tua", 5000000.0, TransactionType.INCOME.name, "01 Oct 2023"),
            TransactionEntity(2, "Transfer UKT", 3500000.0, TransactionType.EXPENSE.name, "02 Oct 2023"),
            TransactionEntity(3, "Makan Siang", 50000.0, TransactionType.EXPENSE.name, "03 Oct 2023")
        )
    )

    fun getAllTransactions(): Flow<List<TransactionEntity>> = transactions

    fun getTransactionById(id: Int): Flow<TransactionEntity?> =
        transactions.map { list -> list.find { it.id == id } }

    fun getTransactionsByType(type: String): Flow<List<TransactionEntity>> =
        transactions.map { list -> list.filter { it.type == type } }

    suspend fun insertTransaction(entity: TransactionEntity) {
        val currentList = transactions.value.toMutableList()
        val newId = (currentList.maxOfOrNull { it.id } ?: 0) + 1
        currentList.add(entity.copy(id = newId))
        transactions.value = currentList
    }

    suspend fun updateTransaction(entity: TransactionEntity) {
        val currentList = transactions.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == entity.id }
        if (index != -1) {
            currentList[index] = entity
            transactions.value = currentList
        }
    }

    suspend fun deleteTransaction(id: Int) {
        transactions.value = transactions.value.filter { it.id != id }
    }
}