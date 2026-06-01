package com.mywallet.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import app.cash.sqldelight.db.SqlDriver
import com.mywallet.data.model.TransactionEntity
import com.mywallet.db.WalletDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionLocalDataSource(driver: SqlDriver) {
    private val database = WalletDatabase(driver)
    private val queries = database.transactionQueries

    fun getAllTransactions(): Flow<List<TransactionEntity>> =
        queries.selectAllTransactions().asFlow().mapToList(Dispatchers.Default).map { list ->
            list.map { it.toDataEntity() }
        }

    fun getTransactionById(id: Int): Flow<TransactionEntity?> =
        queries.selectTransactionById(id.toLong()).asFlow().mapToOneOrNull(Dispatchers.Default).map { it?.toDataEntity() }

    fun getTransactionsByType(type: String): Flow<List<TransactionEntity>> =
        queries.selectTransactionsByType(type).asFlow().mapToList(Dispatchers.Default).map { list ->
            list.map { it.toDataEntity() }
        }

    fun insertTransaction(entity: TransactionEntity) {
        queries.insertTransaction(entity.title, entity.amount, entity.type, entity.category, entity.date, entity.time, if (entity.isRecurring) 1L else 0L)
    }

    fun updateTransaction(entity: TransactionEntity) {
        queries.updateTransaction(entity.title, entity.amount, entity.type, entity.category, entity.date, entity.time, if (entity.isRecurring) 1L else 0L, entity.id.toLong())
    }

    fun deleteTransaction(id: Int) {
        queries.deleteTransaction(id.toLong())
    }

    private fun com.mywallet.db.TransactionDbEntity.toDataEntity() = TransactionEntity(
        id = id.toInt(),
        title = title,
        amount = amount,
        type = type,
        category = category,
        date = date,
        time = time,
        isRecurring = isRecurring == 1L
    )
}
