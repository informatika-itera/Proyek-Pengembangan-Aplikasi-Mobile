package com.mywallet.fakes

import com.mywallet.domain.model.Transaction
import com.mywallet.domain.model.TransactionType
import com.mywallet.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeTransactionRepository : TransactionRepository {
    private val transactions = MutableStateFlow<List<Transaction>>(emptyList())

    override fun getAllTransactions(): Flow<List<Transaction>> = transactions

    override fun getTransactionById(id: Int): Flow<Transaction?> = 
        transactions.map { list -> list.find { it.id == id } }

    override fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> =
        transactions.map { list -> list.filter { it.type == type } }

    override suspend fun insertTransaction(transaction: Transaction) {
        val currentList = transactions.value.toMutableList()
        val newTransaction = if (transaction.id == 0) {
            transaction.copy(id = (currentList.maxOfOrNull { it.id } ?: 0) + 1)
        } else {
            transaction
        }
        currentList.add(newTransaction)
        transactions.value = currentList
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        val currentList = transactions.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == transaction.id }
        if (index != -1) {
            currentList[index] = transaction
            transactions.value = currentList
        }
    }

    override suspend fun deleteTransaction(id: Int) {
        val currentList = transactions.value.toMutableList()
        currentList.removeAll { it.id == id }
        transactions.value = currentList
    }

    fun emit(list: List<Transaction>) {
        transactions.value = list
    }
}
