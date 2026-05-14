package com.example.pocketguard.domain.model // Sesuaikan dengan namespace baru Anda

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val description: String,
    val category: TransactionCategory,
    val type: TransactionType, // INCOME atau EXPENSE
    val createdAt: Long // Gunakan timestamp (ms)
) {
    // Properti pembantu untuk menampilkan ringkasan deskripsi (Requirement UI/UX)
    val preview: String
        get() = if (description.length > 30) description.take(27) + "..." else description

    // Validasi dasar (Requirement State Management)
    val isValid: Boolean
        get() = amount > 0 && description.isNotBlank()
}

enum class TransactionType {
    INCOME, EXPENSE
}

enum class TransactionCategory(val displayName: String) {
    FOOD("Makanan"),
    TRANSPORT("Transportasi"),
    BILLS("Tagihan"),
    SALARY("Gaji"), // Tambahan untuk Income
    OTHER("Lainnya");

    companion object {
        fun fromString(value: String): TransactionCategory {
            return entries.find { it.name == value } ?: OTHER
        }
    }
}