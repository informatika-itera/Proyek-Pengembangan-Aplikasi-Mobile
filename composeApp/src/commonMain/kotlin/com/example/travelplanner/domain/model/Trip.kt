package com.example.travelplanner.domain.model

data class Trip(
    val id: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val duration: String,
    val vibe: String,
    val itineraryItems: List<ItineraryItem>
)

data class ItineraryItem(
    val time: String,
    val activity: String,
    val icon: String
)