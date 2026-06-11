package com.example.inventra.domain.model

import kotlinx.datetime.Instant

data class BorrowRecord(
    val id: Long = 0,
    val remoteId: String? = null,
    val itemId: Long,
    val borrowerId: String = "",
    val itemName: String,
    val borrowerName: String,
    val borrowerDivision: String = "",
    val borrowDate: Instant,
    val dueDate: Instant,
    val returnDate: Instant? = null,
    val status: BorrowStatus = BorrowStatus.ACTIVE,
    val fineAmount: Long = 0,
    val returnProofUrl: String? = null
)

enum class BorrowStatus { PENDING, ACTIVE, RETURNED, OVERDUE, PENDING_RETURN }