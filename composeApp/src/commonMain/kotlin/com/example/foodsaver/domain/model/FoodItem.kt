package com.example.foodsaver.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Model data utama untuk item makanan di FoodSaver.
 */
@Serializable
data class FoodItem(
    val id: Long = 0,
    val name: String,
    val quantity: String,
    val category: String,
    val expiryDate: Instant,
    val createdAt: Instant,
    val isWasted: Boolean = false
)
