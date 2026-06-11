package com.example.travelplanner.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TripFinanceSummary(
    val id: String,
    val city: String,
    val duration: String,
    val startDate: String,
    val totalExpense: Double
)
