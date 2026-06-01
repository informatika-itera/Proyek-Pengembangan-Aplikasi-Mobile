package com.mywallet.data.model

import com.mywallet.domain.model.Transaction
import com.mywallet.domain.model.TransactionType

data class TransactionEntity(
    val id: Int = 0,
    val title: String,
    val amount: Double,
    val type: String,
    val category: String,
    val date: String,
    val time: String,
    val isRecurring: Boolean = false // Added recurring field
)

fun TransactionEntity.toDomain() = Transaction(
    id = id,
    title = title,
    amount = amount,
    type = TransactionType.valueOf(type),
    category = category,
    date = date,
    time = time,
    isRecurring = isRecurring
)

fun Transaction.toEntity() = TransactionEntity(
    id = id,
    title = title,
    amount = amount,
    type = type.name,
    category = category,
    date = date,
    time = time,
    isRecurring = isRecurring
)