package com.example.foodsaver.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlin.math.abs

@Serializable
data class FoodItem(
    val id: Long = 0,
    val name: String,
    val quantity: Double,
    val unit: String,
    val buyDate: Instant,
    val expiryDate: Instant,
    val category: String,
    val storageLocation: String = "Kulkas",
    val notes: String? = null,
    val isConsumed: Boolean = false,
    val isDiscarded: Boolean = false
) {
    fun getStatus(): FoodStatus {
        val daysRemaining = getDaysRemaining()

        return when {
            daysRemaining < 0 -> FoodStatus.EXPIRED
            daysRemaining == 0 -> FoodStatus.EXPIRED_TODAY
            daysRemaining in 1..3 -> FoodStatus.NEAR_EXPIRY
            else -> FoodStatus.SAFE
        }
    }

    fun getDaysRemaining(): Int {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val expiry = expiryDate.toLocalDateTime(TimeZone.currentSystemDefault()).date
        return today.daysUntil(expiry)
    }

    fun getStatusLabel(): String {
        val daysRemaining = getDaysRemaining()
        return when {
            daysRemaining < 0 -> "Sudah lewat ${abs(daysRemaining)} hari"
            daysRemaining == 0 -> "Terakhir hari ini"
            daysRemaining == 1 -> "Segera habis besok"
            daysRemaining in 2..3 -> "$daysRemaining hari lagi"
            else -> "Masih segar, $daysRemaining hari lagi"
        }
    }

    companion object {
        val CATEGORIES = listOf("Buah", "Sayur", "Daging", "Susu & Telur", "Minuman", "Camilan", "Bumbu", "Lainnya")
        val STORAGE_LOCATIONS = listOf("Kulkas", "Freezer", "Rak Dapur", "Meja Makan")
        val UNITS = listOf("pcs", "gram", "kg", "botol", "bungkus", "liter")
    }
}

enum class FoodStatus {
    SAFE, NEAR_EXPIRY, EXPIRED, EXPIRED_TODAY
}
