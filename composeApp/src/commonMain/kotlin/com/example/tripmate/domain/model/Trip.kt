package com.example.tripmate.domain.model

data class Trip(
    val id: Long = 0,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val budget: Double,
    val createdAt: Long
)