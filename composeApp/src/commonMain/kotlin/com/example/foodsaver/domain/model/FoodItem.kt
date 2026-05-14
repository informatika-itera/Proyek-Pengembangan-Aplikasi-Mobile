package com.example.foodsaver.domain.model

import kotlinx.datetime.Instant

data class FoodItem(
    val id: Long = 0,
    val name: String,
    val quantity: Double,
    val unit: String,
    val expiryDate: Instant,
    val category: String,
    val isConsumed: Boolean = false
)
