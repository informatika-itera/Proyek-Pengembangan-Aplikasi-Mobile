package com.example.mapenumkm.data.repository

import com.example.mapenumkm.data.local.NoteDatabase
import com.example.mapenumkm.data.local.GetTransactionItems
import com.example.mapenumkm.data.local.entity.toDomain
import com.example.mapenumkm.domain.model.Transaction
import com.example.mapenumkm.domain.repository.TransactionRepository
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class TransactionRepositoryImpl(
    private val db: NoteDatabase
) : TransactionRepository {
    private val queries = db.noteQueries

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return queries.getAllTransactions().asFlow().mapToList(Dispatchers.IO).map { entities ->
            entities.map { entity ->
                try {
                    val items: List<GetTransactionItems> = queries.getTransactionItems(entity.id).executeAsList()
                    entity.toDomain(items)
                } catch (e: Exception) {
                    // Fallback to empty items with explicit type
                    entity.toDomain(emptyList<GetTransactionItems>())
                }
            }
        }
    }

    override suspend fun insertTransaction(transaction: Transaction): Long {
        return db.transactionWithResult {
            queries.insertTransaction(
                subtotal = transaction.subtotal,
                discount = transaction.discount,
                total = transaction.total,
                payment_amount = transaction.paymentAmount,
                change_amount = transaction.changeAmount,
                created_at = transaction.createdAt.toEpochMilliseconds()
            )
            val transactionId = queries.lastInsertId().executeAsOne()
            
            transaction.items.forEach { item ->
                queries.insertTransactionItem(
                    transaction_id = transactionId,
                    product_id = item.productId,
                    product_name = item.productName,
                    product_price = item.productPrice,
                    quantity = item.quantity.toLong(),
                    product_image_url = item.imageUrl
                )
                // Reduce stock of the product
                queries.reduceStock(
                    stockReduction = item.quantity.toLong(),
                    updatedAt = Clock.System.now().toEpochMilliseconds(),
                    id = item.productId
                )
            }
            transactionId
        }
    }

    override suspend fun deleteTransaction(id: Long) {
        queries.deleteTransaction(id)
    }
}