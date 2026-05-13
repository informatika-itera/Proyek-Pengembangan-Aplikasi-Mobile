package com.example.pantaujompo.domain.model

data class ActivityModel(
    val id: Long = 0,
    val type: String,
    val durationMinutes: Long,
    val distanceKm: Double,
    val caloriesBurned: Long,
    val dateTimestamp: Long,
    val notes: String? = null
)