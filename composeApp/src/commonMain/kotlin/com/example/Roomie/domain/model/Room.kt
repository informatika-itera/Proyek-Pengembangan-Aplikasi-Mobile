package com.example.Roomie.domain.model

enum class RoomStatus {
    AVAILABLE,
    BOOKED,
    MAINTENANCE
}

enum class RoomType {
    REGULAR,
    AULA
}

data class Room(
    val id: String,
    val name: String,
    val floor: Int,
    val status: RoomStatus,
    val type: RoomType = RoomType.REGULAR,
    val capacity: Int = 40,
    val hasAc: Boolean = true,
    val hasProjector: Boolean = true
)
