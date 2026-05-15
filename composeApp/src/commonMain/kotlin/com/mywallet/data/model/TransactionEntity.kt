package com.mywallet.data.model

import com.mywallet.domain.model.Transaction
import com.mywallet.domain.model.TransactionType

data class TransactionEntity(
    val id: Int = 0,
    val title: String,
    val amount: Double,
    val type: String,
    val date: String
)

fun TransactionEntity.toDomain() = Transaction(
    id = id,
    title = title,
    amount = amount,
    type = TransactionType.valueOf(type),
    date = date
)

fun Transaction.toEntity() = TransactionEntity(
    id = id,
    title = title,
    amount = amount,
    type = type.name,
    date = date
)