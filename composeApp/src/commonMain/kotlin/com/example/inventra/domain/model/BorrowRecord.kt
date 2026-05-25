package com.example.inventra.domain.model

import kotlinx.datetime.Instant

data class BorrowRecord(
    val id: Long = 0,
    val itemId: Long,
    val itemName: String,
    val borrowerName: String,
    val borrowDate: Instant,
    val dueDate: Instant,
    val returnDate: Instant? = null,
    val status: BorrowStatus = BorrowStatus.ACTIVE,
    val fineAmount: Long = 0
)

enum class BorrowStatus { ACTIVE, RETURNED, OVERDUE }
