package com.example.mapenumkm.data.local.entity

import com.example.mapenumkm.data.local.TransactionEntity
import com.example.mapenumkm.data.local.TransactionItemEntity
import com.example.mapenumkm.data.local.GetTransactionItems
import com.example.mapenumkm.domain.model.Transaction
import com.example.mapenumkm.domain.model.TransactionItem
import kotlinx.datetime.Instant

/**
 * Extension function to convert TransactionEntity and its items to domain model.
 * This version uses GetTransactionItems which is the result of the SQLDelight JOIN query.
 */
fun TransactionEntity.toDomain(items: List<GetTransactionItems>): Transaction {
    return Transaction(
        id = id,
        subtotal = subtotal,
        discount = discount,
        total = total,
        paymentAmount = payment_amount,
        changeAmount = change_amount,
        createdAt = Instant.fromEpochMilliseconds(created_at),
        items = items.map { 
            TransactionItem(
                productId = it.product_id,
                productName = it.product_name,
                productPrice = it.product_price,
                quantity = it.quantity.toInt(),
                imageUrl = it.product_image_url
            )
        }
    )
}

/**
 * Alternative for raw TransactionItemEntity if used.
 */
fun TransactionItemEntity.toTransactionItem(): TransactionItem {
    return TransactionItem(
        productId = product_id,
        productName = product_name,
        productPrice = product_price,
        quantity = quantity.toInt(),
        imageUrl = product_image_url
    )
}
