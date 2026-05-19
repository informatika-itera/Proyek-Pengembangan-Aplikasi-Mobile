package com.example.foodsaver.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class FoodItem(
    val id: Long = 0,
    val name: String,
    val quantity: Double,
    val unit: String,
    val category: String,
    val expiryDate: Instant,
    val isConsumed: Boolean = false
)
