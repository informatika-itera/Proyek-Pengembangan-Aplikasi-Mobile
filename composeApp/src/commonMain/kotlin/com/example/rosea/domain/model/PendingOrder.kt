package com.example.rosea.domain.model

data class PendingOrder(
    val id: Long,
    val totalPrice: Double,
    val itemsSummary: String,
    val createdAt: Long,
    val status: String
)