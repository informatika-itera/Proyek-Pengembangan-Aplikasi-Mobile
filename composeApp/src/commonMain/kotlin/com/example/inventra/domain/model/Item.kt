package com.example.inventra.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Item(
    val id: Long = 0,
    val remoteId: String? = null,
    val name: String,
    val description: String = "",
    val category: ItemCategory,
    val location: String,
    val totalStock: Int,
    val availableStock: Int,
    val condition: ItemCondition = ItemCondition.GOOD,
    val picName: String = "",         // Person In Charge
    val picPhone: String = "",        // PIC Phone Number
    val imageUrl: String? = null,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now()
) {
    val isBorrowable: Boolean get() = availableStock > 0
    val statusLabel: String get() = if (isBorrowable) "Available" else "Borrowed"
}

enum class ItemCategory(val displayName: String) {
    ALL("Semua"),
    MEDICAL("Medis"),
    FOOD("Konsumsi"),
    FLAG("Bendera"),
    ELECTRONICS("Elektronik"),
    OTHER("Lainnya")
}

enum class ItemCondition(val displayName: String) {
    NEW("New"),
    GOOD("Good"),
    FAIR("Fair"),
    POOR("Poor")
}
