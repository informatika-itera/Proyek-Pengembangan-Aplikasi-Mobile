package com.example.rosea.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CartItem(
    val id: Long,
    val productId: Long,
    val productName: String,
    val brand: String,
    val price: Double,
    val imageUrl: String,
    val quantity: Long,
    val addedAt: Long
)