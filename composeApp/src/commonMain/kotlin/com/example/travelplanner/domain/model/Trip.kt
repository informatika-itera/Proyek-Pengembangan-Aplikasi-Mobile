package com.example.travelplanner.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Trip(
    val id: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val duration: String,
    val vibe: String,
    val itineraryItems: List<ItineraryItem>
)

@Serializable
data class ItineraryItem(
    val time: String,
    val activity: String,
    val icon: String,
    val priceRange: String = "",
    val mapsUrl: String = "",
    val placeName: String = "",
    val activityEn: String = "",
    val placeNameEn: String = ""
)