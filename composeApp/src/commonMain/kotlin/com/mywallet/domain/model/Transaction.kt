package com.mywallet.domain.model

enum class TransactionType {
    INCOME, EXPENSE
}

data class Transaction(
    val id: Int,
    val title: String,
    val amount: Double,
    val type: TransactionType,
    val category: String, // Added category field
    val date: String,
    val time: String,
    val isRecurring: Boolean = false // Added recurring field
)
