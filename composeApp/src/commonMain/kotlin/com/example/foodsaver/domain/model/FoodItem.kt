package com.example.foodsaver.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class FoodItem(
    val id: Long = 0,
    val name: String,
    val quantity: Double,
    val unit: String,
    val category: String,
    val expiryDate: Instant,
    val storageLocation: String = "Kulkas",
    val notes: String? = null,
    val isConsumed: Boolean = false
) {
    fun getStatus(): FoodStatus {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val expiry = expiryDate.toLocalDateTime(TimeZone.currentSystemDefault()).date
        val daysRemaining = today.daysUntil(expiry)

        return when {
            daysRemaining < 0 -> FoodStatus.EXPIRED
            daysRemaining <= 3 -> FoodStatus.NEAR_EXPIRY
            else -> FoodStatus.SAFE
        }
    }

    fun getDaysRemaining(): Int {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val expiry = expiryDate.toLocalDateTime(TimeZone.currentSystemDefault()).date
        return today.daysUntil(expiry)
    }
}

enum class FoodStatus {
    SAFE, NEAR_EXPIRY, EXPIRED
}
