package com.example.pocketguard.data.local.entity

import com.example.pocketguard.data.local.TransactionEntity
import com.example.pocketguard.domain.model.Transaction
import com.example.pocketguard.domain.model.TransactionCategory
import com.example.pocketguard.domain.model.TransactionType

fun TransactionEntity.toTransaction(): Transaction {
    return Transaction(
        id = id,
        amount = amount,
        description = description,
        category = TransactionCategory.fromString(category),
        type = TransactionType.valueOf(type),
        createdAt = created_at
    )
}

fun List<TransactionEntity>.toTransactionList(): List<Transaction> {
    return map { it.toTransaction() }
}