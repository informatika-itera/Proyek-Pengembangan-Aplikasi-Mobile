package com.example.pantaujompo.domain.model

data class ActivityModel(
    val id: Long? = null,
    val title: String,
    val distanceKm: Double,
    val durationMins: Int,
    val calories: Int,
    val timestamp: Long
)