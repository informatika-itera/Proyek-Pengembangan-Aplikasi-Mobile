package com.example.travelplanner.domain.model

data class Expense(
    val id: String,
    val tripId: String,
    val namaItem: String,
    val nominal: Double,
    val kategori: String,
    val createdAt: Long
)
