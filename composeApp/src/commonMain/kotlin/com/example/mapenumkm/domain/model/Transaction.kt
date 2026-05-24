package com.example.mapenumkm.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    val id: Long = 0,
    val items: List<TransactionItem>,
    val subtotal: Double,
    val discount: Double = 0.0,
    val total: Double,
    val paymentAmount: Double,
    val changeAmount: Double,
    val createdAt: Instant = Clock.System.now()
)

@Serializable
data class TransactionItem(
    val productId: Long,
    val productName: String,
    val productPrice: Double,
    val quantity: Int,
    val imageUrl: String? = null
) {
    val totalPrice: Double
        get() = productPrice * quantity
}