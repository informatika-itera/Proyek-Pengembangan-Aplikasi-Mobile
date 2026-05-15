package com.example.Roomie.domain.model

enum class RoomStatus {
    AVAILABLE,
    BOOKED,
    MAINTENANCE
}

data class Room(
    val id: String,
    val name: String,
    val status: RoomStatus,
    val row: Int,
    val col: Int,
    val capacity: Int = 40,
    val hasAc: Boolean = true,
    val hasProjector: Boolean = true
)
