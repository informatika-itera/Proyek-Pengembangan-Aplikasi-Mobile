package com.example.Roomie.domain.model

enum class BookingStatus {
    UPCOMING,
    ONGOING,
    COMPLETED,
    CANCELLED
}

data class Booking(
    val id: String,
    val roomId: String,
    val roomName: String,
    val buildingName: String,
    val startTime: Long,
    val endTime: Long,
    val status: BookingStatus,
    val subject: String? = null // For class schedule integration
)
