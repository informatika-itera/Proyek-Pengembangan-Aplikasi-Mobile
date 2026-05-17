package com.mywallet.domain.model

enum class TransactionType {
    INCOME, EXPENSE
}

data class Transaction(
    val id: Int,
    val title: String,
    val amount: Double,
    val type: TransactionType,
    val date: String
)
